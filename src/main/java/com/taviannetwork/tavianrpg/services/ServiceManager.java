package com.taviannetwork.tavianrpg.services;

import com.taviannetwork.tavianrpg.TavianRPG;
import com.taviannetwork.tavianrpg.graph.Graph;
import com.taviannetwork.tavianrpg.graph.Node;
import com.taviannetwork.tavianrpg.graph.SortingUtils;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Singleton
public class ServiceManager {
    @Getter
    @NotNull
    private List<Service> services = new ArrayList<>();

    @Inject
    private TavianRPG plugin;
    @Inject
    private Injector injector;

    private BukkitTask serviceTickTask;

    public void start() {
        if(!checkDependencies()) {
            services.clear();
            return;
        }

        for(Service service : services) {
            ServiceInfo info = service.getClass().getAnnotation(ServiceInfo.class);
            if(info == null) {
                plugin.getLogger().severe("Service does not service info");
                return;
            }

            plugin.getLogger().finer("enable " + info.serviceName());

            try {
                service.start();
            } catch(Exception ex) {
                plugin.getLogger().severe("Error while starting " + info.serviceName());
                ex.printStackTrace();
                return;
            }

            plugin.getLogger().info("Enabled " + info.serviceName() + " v" + info.serviceVersion() + " by " + info.serviceAuthor());

            if(service instanceof Listener) {
                Bukkit.getServer().getPluginManager().registerEvents((Listener) service, plugin);
            }
        }

        serviceTickTask = Bukkit.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    public void tick() {
        services.forEach(Service::tick);
    }

    public void stop() {
        serviceTickTask.cancel();
        serviceTickTask = null;

        List<Service> reversedServices = new ArrayList<>(this.services);
        Collections.reverse(reversedServices);

        for(Service service : services) {
            ServiceInfo info = service.getClass().getAnnotation(ServiceInfo.class);
            if(info == null) {
                plugin.getLogger().severe("Service does not service info");
                return;
            }

            plugin.getLogger().finer("disable " + info.serviceName());

            try {
                service.stop();
            } catch(Exception ex) {
                plugin.getLogger().severe("Error while stopping " + info.serviceName());
                ex.printStackTrace();
                return;
            }

            if(service instanceof Listener) {
                HandlerList.unregisterAll((Listener) service);
            }
        }

        services.clear();
    }

    private boolean checkDependencies() {
        Graph<Class<? extends Service>> graph = new Graph<>();

        for(Service service : services) {
            Node<Class<? extends Service>> serviceNode = new Node<>(service.getClass());

            service.getDependencies().forEach(serviceNode::addDependency);

            graph.addNode(serviceNode);
        }


        Map<Class<? extends Service>, Boolean> dependencyReport = SortingUtils.buildDependencyReport(graph);
        if(dependencyReport.containsValue(false)) {
            StringBuilder reportBuilder = new StringBuilder();

            reportBuilder.append("These dependencies aren't met:\n");
            dependencyReport.entrySet()
                    .stream()
                    .filter(e -> e.getValue().equals(false))
                    .forEach(e -> reportBuilder.append("    ").append(e.getClass().getSimpleName()).append('\n'));
            plugin.getLogger().severe(reportBuilder.toString());

            graph.clear();
            dependencyReport.clear();
            return false;
        }

        List<Service> services = new ArrayList<>();
        SortingUtils.topoSort(graph).forEach(s -> services.add(getService(s)));

        this.services.clear();
        this.services = services;

        graph.clear();
        dependencyReport.clear();
        return true;
    }

    public <S extends Service> S createService(Class<S> serviceClass) {
        S service = injector.getInstance(serviceClass);
        services.add(service);
        service.init();
        return service;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <S extends Service> S getService(Class<S> serviceClass) {
        return (S) services.stream().filter(s -> s.getClass().equals(serviceClass)).findFirst()
                .orElseThrow(() -> new NoSuchServiceException(serviceClass));
    }
}
