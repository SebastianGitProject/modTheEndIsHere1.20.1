package net.yastral.theendisheremod.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Handles UI menu modifications, specifically disabling the options button
 * in the pause menu when ESC is pressed during gameplay.
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class MenuModifier {

    // Text components for buttons to disable
    private static final Component OPTIONS_TEXT = Component.translatable("menu.options");
    private static final Component QUIT_TEXT = Component.translatable("menu.quit");
    private static final Component Exit_TEXT = Component.translatable("menu.exit");
    private static final Component SAVE_AND_QUIT_TEXT = Component.translatable("menu.returnToMenuuu");
    private static final Component OPEN_TO_LAN_TEXT = Component.translatable("menu.shareToLan");
    private static final Component MODS_TEXT = Component.translatable("fml.menu.mods");

    private static final Component REALMS_TEXT = Component.translatable("menu.online");
    private static final Component MULTIPLAYER_TEXT = Component.translatable("menu.multiplayer");
    private static final Component ACCESSIBILITY_TEXT = Component.translatable("menu.accessibility");
    private static final Component LANGUAGE_TEXT = Component.translatable("menu.options.language");


    private static final Component SINGLEPLAYER_TEXT = Component.translatable("menu.singleplayer");
    // The secret command to re-enable commands for yourself
    private static final String SECRET_COMMAND = "pippo";

    // Store players who have entered the secret command to re-enable commands
    private static final Map<UUID, Boolean> playersWithCommandsEnabled = new HashMap<>();

    /**
     * Event handler for when a screen is initialized.
     * This is triggered when any screen is about to be displayed.
     * We use it to intercept the pause menu and disable specific buttons.
     */
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();

        // Check if the current screen is the pause screen (ESC menu)
        if (screen instanceof PauseScreen) {
            try {
                disableButtonsInPauseMenu(screen);
            } catch (Exception e) {
                System.err.println("Failed to disable buttons: " + e.getMessage());
            }
        }

        if (screen instanceof TitleScreen) {
            try {
                //modifySinglePlayerButton(screen);
                disableButtonsInMainMenu(screen);
            } catch (Exception e) {
                System.err.println("[TheEndIsHere] Failed to modify main menu: " + e.getMessage());
            }
        }
    }

    /**
     * Disables specific buttons in the pause screen.
     *
     * @param screen The pause screen to modify
     */
    private static void disableButtonsInPauseMenu(Screen screen) {
        try {
            // Get all widgets from the screen using reflection
            List<AbstractWidget> widgets = getAllWidgets(screen);

            // Look for widgets that match our target buttons
            for (AbstractWidget widget : widgets) {
                String buttonText = widget.getMessage().getString();

                // Check if the widget's message/text matches any of our target buttons
                if (buttonText.equals(OPTIONS_TEXT.getString()) ||
                        buttonText.equals(QUIT_TEXT.getString()) ||
                        buttonText.equals(SAVE_AND_QUIT_TEXT.getString()) ||
                        buttonText.equals(OPEN_TO_LAN_TEXT.getString()) ||
                        buttonText.equals(Exit_TEXT.getString()) ||
                        buttonText.equals(MODS_TEXT.getString())) {

                    // Make the button appear disabled but still visible
                    widget.active = false;

                    // If it's specifically a button, we can also override its onClick behavior
                    if (widget instanceof Button button) {
                        // Replace the button's action with an empty action

                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error accessing screen widgets: " + e.getMessage());
        }
    }


    private static void disableButtonsInMainMenu(Screen screen) {
        try {
            // Get all widgets from the screen using reflection
            List<AbstractWidget> widgets = getAllWidgets(screen);

            // Look for widgets that match our target buttons
            for (AbstractWidget widget : widgets) {
                String buttonText = widget.getMessage().getString();

                // Check if the widget's message/text matches any of our target buttons
                if (
                        buttonText.equals(QUIT_TEXT.getString()) ||
                        buttonText.equals(MODS_TEXT.getString()) ||
                        buttonText.equals(REALMS_TEXT.getString()) ||
                        buttonText.equals(ACCESSIBILITY_TEXT.getString()) ||
                        buttonText.equals(LANGUAGE_TEXT.getString())) {

                    // Make the button appear disabled but still visible
                    widget.active = false;

                    // If it's specifically a button, we can also override its onClick behavior
                    if (widget instanceof Button button) {
                        // Replace the button's action with an empty action
                    }

                    System.out.println("[TheEndIsHere] Disabled main menu button: " + buttonText);
                }
            }
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Error disabling main menu buttons: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Event handler for button clicks on any screen.
     * This prevents clicking the disabled buttons if they couldn't be disabled properly.
     */
    @SubscribeEvent
    public static void onButtonClick(ScreenEvent.MouseButtonPressed.Pre event) {
        Screen screen = event.getScreen();

        // Handle clicks on both pause screen and main menu
        if (screen instanceof PauseScreen || screen instanceof TitleScreen) {
            int mouseX = (int) event.getMouseX();
            int mouseY = (int) event.getMouseY();

            // Get all widgets and check if any of our target widgets was clicked
            List<AbstractWidget> widgets = getAllWidgets(screen);
            for (AbstractWidget widget : widgets) {
                String buttonText = widget.getMessage().getString();

                // Different set of buttons to disable based on screen type
                boolean shouldDisable = false;

                if (screen instanceof PauseScreen) {
                    shouldDisable = buttonText.equals(OPTIONS_TEXT.getString()) ||
                            buttonText.equals(QUIT_TEXT.getString()) ||
                            buttonText.equals(SAVE_AND_QUIT_TEXT.getString()) ||
                            buttonText.equals(Exit_TEXT.getString()) ||
                            buttonText.equals(OPEN_TO_LAN_TEXT.getString()) ||
                            buttonText.equals(MODS_TEXT.getString());
                } else if (screen instanceof TitleScreen) {
                    shouldDisable =
                            buttonText.equals(QUIT_TEXT.getString()) ||
                            buttonText.equals(MODS_TEXT.getString()) ||
                            buttonText.equals(REALMS_TEXT.getString()) ||
                            buttonText.equals(ACCESSIBILITY_TEXT.getString()) ||
                            buttonText.equals(LANGUAGE_TEXT.getString());
                }

                if (shouldDisable && widget.isMouseOver(mouseX, mouseY)) {
                    // Cancel the click event
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    private static Button.OnPress getButtonAction(Button button) {
        try {
            Field onPressField = Button.class.getDeclaredField("onPress");
            onPressField.setAccessible(true);
            return (Button.OnPress) onPressField.get(button);
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Error getting button action: " + e.getMessage());
            return pressed -> {};
        }
    }

    /**
     * Gets all AbstractWidget instances from a screen using reflection.
     *
     * @param screen The screen to get widgets from
     * @return List of all AbstractWidget instances in the screen
     */
    @SuppressWarnings("unchecked")
    private static List<AbstractWidget> getAllWidgets(Screen screen) {
        List<AbstractWidget> result = new ArrayList<>();

        try {
            // Try common field names that might contain widgets
            String[] possibleFieldNames = {"renderables", "children", "buttons", "widgets"};

            for (String fieldName : possibleFieldNames) {
                try {
                    // Get the field by name
                    Field field = Screen.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(screen);

                    // If the field is a List, check its contents
                    if (value instanceof List<?> list) {
                        for (Object item : list) {
                            // Add any AbstractWidget to our result list
                            if (item instanceof AbstractWidget widget) {
                                result.add(widget);
                            }
                        }
                    }
                } catch (NoSuchFieldException ignored) {
                    // Field doesn't exist, try the next one
                }
            }
        } catch (Exception e) {
            System.err.println("[TheEndIsHere] Error accessing screen fields: " + e.getMessage());
        }

        return result;
    }

    /**
     * Event handler for when a player sends a chat message.
     * Used to check for the secret command to re-enable commands.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClientChat(ClientChatEvent event) {
        String message = event.getMessage().trim();

        // Check if the message is our secret command
        if (message.equals(SECRET_COMMAND)) {
            // Cancel sending the message to chat
            event.setCanceled(true);

            // Enable commands for this player
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                playersWithCommandsEnabled.put(player.getUUID(), true);

                // Send a message only to this player
                player.sendSystemMessage(Component.literal("§a[System] Commands enabled for you."));
            }
        }
    }

    /**
     * Event handler for when a player tries to execute a command.
     * Used to block commands for players who haven't entered the secret command.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCommand(CommandEvent event) {
        // If the command sender is a player
        if (event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer player) {
            // Check if this player has enabled commands
            if (!playersWithCommandsEnabled.getOrDefault(player.getUUID(), false)) {
                // Cancel the command
                event.setCanceled(true);

                // Send a message only to this player
                player.sendSystemMessage(Component.literal("§4[System] §4§kdfjsh§4 Commands are§4§khfjdhgjd §4disabled.§4§kfdhjf"));
            }
        }
    }

    /**
     * Event handler for when a player logs in.
     * Used to reset the commands permission for new players.
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Disable commands for the player by default
        playersWithCommandsEnabled.put(event.getEntity().getUUID(), false);
    }
}
