package com.taviannetwork.tavianrpg.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class ChatUtils {
    private final static int CENTER_PX = 154;

    private ChatUtils() {
        throw new AssertionError();
    }

    public static void sendCenteredMessage(CommandSender sender, String message){
        if(message == null || message.equals("")) sender.sendMessage("");
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == ChatColor.COLOR_CHAR){
                previousCode = true;
                continue;
            } else if(previousCode) {
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                } else {
                    isBold = false;
                }
            } else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        sender.sendMessage(sb.toString() + message);
    }
}
