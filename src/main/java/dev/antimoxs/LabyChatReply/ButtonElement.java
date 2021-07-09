package dev.antimoxs.LabyChatReply;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Consumer;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public class ButtonElement extends ControlElement {

    protected final Button button;
    protected boolean enabled;
    private Runnable listener;

    public ButtonElement(String displayName, IconData iconData, String text, Runnable listener) {
        super(displayName, iconData);
        this.button = new Button(0, 0, 0, 20, ITextComponent.getTextComponentOrEmpty(text), new Button.IPressable() {
            @Override
            public void onPress(Button p_onPress_1_) {
                if (ButtonElement.this.listener != null) {
                    ButtonElement.this.listener.run();
                }
            }
        });
        this.listener = listener;
        this.enabled = true;
        this.button.setMessage(ITextComponent.getTextComponentOrEmpty(text));
    }

    public void draw(MatrixStack stack, int x, int y, int maxX, int maxY, int mouseX, int mouseY) {

        button.changeFocus(mouseX > x && mouseX < maxX && mouseY > y && mouseY < maxY);

        super.draw(stack, x, y, maxX, maxY, mouseX, mouseY);
        if (super.displayName != null) {
            LabyMod.getInstance().getDrawUtils().drawRectangle(stack, x - 1, y, x, maxY, Color.GRAY.getRGB());
        }

        int buttonWidth = super.displayName == null ? maxX - x : LabyModCore.getMinecraft().getFontRenderer().getStringWidth(this.button.getMessage().getString()) + 20;
        this.button.setWidth(buttonWidth);
        this.button.active = this.enabled;
        LabyModCore.getMinecraft().setButtonXPosition(this.button, maxX - buttonWidth - 2);
        LabyModCore.getMinecraft().setButtonYPosition(this.button, y + 1);
        this.button.renderButton(stack, mouseX, mouseY, 1);
    }

    public void setEnabled(boolean enabled) {

        this.button.active = enabled;

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.button.mouseClicked((double)mouseX, (double)mouseY, mouseButton);
    }



}
