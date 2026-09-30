package pluginsfix.glowautorestart.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorProcessor {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");
    private static final Pattern LEGACY_PATTERN = Pattern.compile("&([0-9a-fk-orA-FK-OR])");

    private ColorProcessor() {
    }

    public static Component process(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        TextComponent.Builder rootBuilder = Component.text()
            .decoration(TextDecoration.ITALIC, false);

        String normalized = input;
        Matcher hexMatcher = HEX_PATTERN.matcher(normalized);
        StringBuilder sb = new StringBuilder();
        while (hexMatcher.find()) {
            String hex = hexMatcher.group(1);
            hexMatcher.appendReplacement(sb, "§x§" + hex.charAt(0) + "§" + hex.charAt(1) + "§" + hex.charAt(2) + "§" + hex.charAt(3) + "§" + hex.charAt(4) + "§" + hex.charAt(5));
        }
        hexMatcher.appendTail(sb);
        normalized = sb.toString().replace('&', '§');

        TextColor currentColor = NamedTextColor.WHITE;
        boolean bold = false;
        boolean underlined = false;
        boolean strikethrough = false;
        boolean obfuscated = false;

        int length = normalized.length();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char c = normalized.charAt(i);
            if (c == '§' && i + 1 < length) {
                char code = Character.toLowerCase(normalized.charAt(i + 1));
                if (code == 'x' && i + 13 < length) {
                    if (!buffer.isEmpty()) {
                        rootBuilder.append(buildChunk(buffer.toString(), currentColor, bold, underlined, strikethrough, obfuscated));
                        buffer.setLength(0);
                    }
                    try {
                        String hex = "" + normalized.charAt(i + 3) + normalized.charAt(i + 5) + normalized.charAt(i + 7) +
                            normalized.charAt(i + 9) + normalized.charAt(i + 11) + normalized.charAt(i + 13);
                        currentColor = TextColor.fromHexString("#" + hex);
                        i += 13;
                        continue;
                    } catch (Exception ignored) {
                    }
                }

                if (isColorOrFormatCode(code)) {
                    if (!buffer.isEmpty()) {
                        rootBuilder.append(buildChunk(buffer.toString(), currentColor, bold, underlined, strikethrough, obfuscated));
                        buffer.setLength(0);
                    }

                    switch (code) {
                        case '0' -> currentColor = NamedTextColor.BLACK;
                        case '1' -> currentColor = NamedTextColor.DARK_BLUE;
                        case '2' -> currentColor = NamedTextColor.DARK_GREEN;
                        case '3' -> currentColor = NamedTextColor.DARK_AQUA;
                        case '4' -> currentColor = NamedTextColor.DARK_RED;
                        case '5' -> currentColor = NamedTextColor.DARK_PURPLE;
                        case '6' -> currentColor = NamedTextColor.GOLD;
                        case '7' -> currentColor = NamedTextColor.GRAY;
                        case '8' -> currentColor = NamedTextColor.DARK_GRAY;
                        case '9' -> currentColor = NamedTextColor.BLUE;
                        case 'a' -> currentColor = NamedTextColor.GREEN;
                        case 'b' -> currentColor = NamedTextColor.AQUA;
                        case 'c' -> currentColor = NamedTextColor.RED;
                        case 'd' -> currentColor = NamedTextColor.LIGHT_PURPLE;
                        case 'e' -> currentColor = NamedTextColor.YELLOW;
                        case 'f' -> currentColor = NamedTextColor.WHITE;
                        case 'l' -> bold = true;
                        case 'n' -> underlined = true;
                        case 'm' -> strikethrough = true;
                        case 'k' -> obfuscated = true;
                        case 'r' -> {
                            currentColor = NamedTextColor.WHITE;
                            bold = false;
                            underlined = false;
                            strikethrough = false;
                            obfuscated = false;
                        }
                        default -> {
                        }
                    }
                    i++;
                    continue;
                }
            }
            buffer.append(c);
        }

        if (!buffer.isEmpty()) {
            rootBuilder.append(buildChunk(buffer.toString(), currentColor, bold, underlined, strikethrough, obfuscated));
        }

        return rootBuilder.build();
    }

    private static Component buildChunk(
        String text,
        TextColor color,
        boolean bold,
        boolean underlined,
        boolean strikethrough,
        boolean obfuscated
    ) {
        return Component.text(text)
            .color(color)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, bold)
            .decoration(TextDecoration.UNDERLINED, underlined)
            .decoration(TextDecoration.STRIKETHROUGH, strikethrough)
            .decoration(TextDecoration.OBFUSCATED, obfuscated);
    }

    private static boolean isColorOrFormatCode(char code) {
        return (code >= '0' && code <= '9') ||
            (code >= 'a' && code <= 'f') ||
            code == 'k' || code == 'l' || code == 'm' || code == 'n' || code == 'o' || code == 'r';
    }
}
