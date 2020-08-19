package com.taviannetwork.tavianrpg.worlds.wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.taviannetwork.tavianrpg.TavianRPG;
import com.taviannetwork.tavianrpg.services.Service;
import com.taviannetwork.tavianrpg.services.ServiceInfo;
import com.taviannetwork.tavianrpg.worlds.wrapper.auth.AuthKeyResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.naming.AuthenticationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@ServiceInfo(serviceName = "TavianWorldDeliveryWrapper", serviceAuthor = "AtomIsHere", serviceVersion = "1.0.0")
@Singleton
public class TavianWorldDeliveryWrapper implements Service {
    @Inject
    private ObjectMapper mapper;
    @Inject
    private TavianRPG plugin;

    private ConnectionInfo info;
    private boolean admin = false;

    @Override
    public void start() {
        ConnectionInfo info;
        try {
            info = createConnectionInfo();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }

        AuthKeyResponse response;
        try {
            response = mapper.readValue(new URL(info.getBaseUrl("auth/valid", "", true)), AuthKeyResponse.class);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }

        if(!response.isValid()) {
            plugin.getLogger().severe("Invalid world delivery API key!");
            return;
        }

        this.admin = response.getKey().isAdmin();
        this.info = info;
    }

    public CompletableFuture<Void> downloadWorldFile(String worldId, File worldFile) {
        CompletableFuture<Void> download = new CompletableFuture<>();

        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            URL url;
            try {
                url = new URL(info.getBaseUrl("worlds/get", "&name=" + worldId, true));
            } catch (MalformedURLException e) {
                download.completeExceptionally(e);
                return;
            }

            try {
                FileUtils.copyURLToFile(url, worldFile);
            } catch (IOException e) {
                download.completeExceptionally(e);
            }
        });

        return download;
    }

    public CompletableFuture<HttpResponse> uploadWorld(String worldId, File worldFile) {
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();

        if(!admin) {
            future.completeExceptionally(new AuthenticationException("Key is not admin!"));
            return future;
        }

        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            URL url;
            try {
                url = new URL(info.getBaseUrl("worlds/uploadWorld", "&name=" + worldId, true));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
                return;
            }

            HttpEntity entity = MultipartEntityBuilder.create()
                    .addPart("file", new FileBody(worldFile))
                    .build();

            HttpPost request = new HttpPost(url.toString());
            request.setEntity(entity);

            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response;
            try {
                 response = client.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
                return;
            }

            future.complete(response);
        });

        return future;
    }

    private ConnectionInfo createConnectionInfo() throws IOException {
        File file = new File(plugin.getDataFolder(), "worldsapi.json");
        if(!file.exists()) {
            try(InputStream stream = plugin.getResource("worldsapi.json")) {
                FileUtils.copyInputStreamToFile(stream, file);
            }
        }

        return mapper.readValue(file, ConnectionInfo.class);
    }
}
