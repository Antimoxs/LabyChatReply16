package dev.antimoxs.LabyChatReply;

import net.labymod.api.event.Event;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.events.client.chat.MessageSendEvent;
import net.labymod.core.ChatComponent;
import net.labymod.labyconnect.LabyConnect;
import net.labymod.labyconnect.log.MessageChatComponent;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;

public class OnMessageSendListener {

    LabyChatReply LabyChatReply;
    long cooldown = 0;

    public OnMessageSendListener(LabyChatReply LabyChatReply) {

        this.LabyChatReply = LabyChatReply;

    }


    @Subscribe
    public void event(MessageSendEvent event) {

        event.setCancelled(onSend(event.getMessage()));

    }

    public boolean onSend(String s) {

        if (LabyChatReply.cfcUpdate) {

            LabyChatReply.loadCFC();

        }

        if (s.startsWith("/lcr ")) {

            if (s.equals("/lcr reload")) {

                LabyChatReply.loadConfig();
                return true;

            }

        }

        if (s.startsWith("/" + LabyChatReply.getLmcSyntax().trim() + " ")) {

            String[] msg = s.split(" ");
            int synlen = LabyChatReply.getLmcSyntax().split(" ").length;

            if (msg.length < 2 + synlen) return true;

            for (ChatUser u : LabyMod.getInstance().getLabyConnect().getFriends()) {

                if (msg[synlen].equalsIgnoreCase(u.getGameProfile().getName())) {

                    String text = s.replaceFirst(msg[0], "").replaceFirst(msg[1],"").trim();
                    sendLMCMessage(u, text);
                    return true;

                }

            }

            LabyChatReply.sendIngameString(TextFormatting.RED + "Sorry, we can't find the user with name '" + msg[1] + "'.");
            return true;

        }
        else if (s.startsWith("/" + LabyChatReply.getLmrSyntax().trim() + " ")) {

            if (LabyChatReply.lastUser == null) {

                LabyChatReply.sendIngameString(TextFormatting.RED + "Sorry, you don't have any recent conversations.");
                return true;

            }

            String[] msg = s.split(" ");
            int synlen = LabyChatReply.getLmrSyntax().split(" ").length;
            ChatUser u = LabyChatReply.lastUser;

            if (msg.length < 1 + synlen) return true;

            String text = s.substring(LabyChatReply.getLmrSyntax().length() + 1).trim();
            sendLMCMessage(u, text);
            return true;

        }
        else if (LabyChatReply.cfcToggl) {

            for (String k : LabyChatReply.getStorage().keySet()) {

                String v = "/" + LabyChatReply.getStorage().get(k);

                if (s.startsWith(v.trim() + " ")) {

                    String text = s.substring(v.length() + 1).trim();

                    if (getChatUserByName(k) == null) {

                        LabyChatReply.sendIngameString(TextFormatting.RED + "Sorry, we can't find the user with name '" + k + "'.");
                        return true;

                    }

                    ChatUser u = LabyMod.getInstance().getLabyConnect().getChatUser(getChatUserByName(k));

                    sendLMCMessage(u, text);
                    return true;

                }



            }

        }

        return false;

    }

    public void sendLMCMessage(ChatUser u, String text) {

        ChatUser me = LabyMod.getInstance().getLabyConnect().getClientProfile().buildClientUser();
        LabyConnect c = LabyMod.getInstance().getLabyConnect();
        LabyChatReply.lastUser = u;

        if (cooldown + 1000L > System.currentTimeMillis()) {

            LabyChatReply.sendIngameString(TextFormatting.RED + "Hey! You are sending messages to fast.");
            return;

        }

        cooldown = System.currentTimeMillis();

        Minecraft.getInstance().runAsync(() -> {

            c.getChatlogManager().getChat(u).addMessage(new MessageChatComponent(me.getGameProfile().getName(), System.currentTimeMillis(), text));
            c.getChatlogManager().saveChatlogs(me.getGameProfile().getId());
            if (LabyChatReply.msgToggl) {
                LabyChatReply.sendIngameString(TextFormatting.WHITE + " " + TextFormatting.BOLD +
                        "[" + me.getGameProfile().getName() +
                        TextFormatting.WHITE + "" + TextFormatting.BOLD +
                        "] -> [" + u.getGameProfile().getName() +
                        TextFormatting.WHITE + "" + TextFormatting.BOLD +
                        "]: " + TextFormatting.GRAY + text + TextFormatting.RESET);
            }

        });

    }

    private ChatUser getChatUserByName(String name) {

        for (ChatUser u : LabyMod.getInstance().getLabyConnect().getFriends()) {

            // Check for name then uuid
            if (u.getGameProfile().getName().equals(name)) return u;
            if (u.getGameProfile().getId().toString().equals(name)) return u;

        }

        return null;

    }

}
