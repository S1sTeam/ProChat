package me.prochat.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FormatManager {

    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    private static final Pattern GRADIENT_PATTERN =
            Pattern.compile("<gradient:#([a-fA-F0-9]{6})#([a-fA-F0-9]{6})>(.*?)</gradient>", Pattern.DOTALL);
    private static final Pattern ANIM_GRADIENT_PATTERN =
            Pattern.compile("<anim_gradient>(.*?)</anim_gradient>", Pattern.DOTALL);
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.builder()
                    .character('&')
                    .hexColors()
                    .build();

    private static long animOffset = 0;

    public static void tickAnimation() {
        animOffset++;
    }

    public static Component parse(String input) {
        return parse(input, null);
    }

    public static Component parse(String input, List<String> cycleColors) {
        if (input == null || input.isEmpty()) return Component.empty();

        Matcher animMatcher = ANIM_GRADIENT_PATTERN.matcher(input);
        List<Component> parts = new ArrayList<>();
        int lastEnd = 0;

        while (animMatcher.find()) {
            if (animMatcher.start() > lastEnd) {
                parts.add(parseStandard(input.substring(lastEnd, animMatcher.start())));
            }
            String text = animMatcher.group(1);
            parts.add(createAnimatedGradient(text, cycleColors));
            lastEnd = animMatcher.end();
        }
        if (lastEnd < input.length()) {
            parts.add(parseStandard(input.substring(lastEnd)));
        }

        if (parts.isEmpty()) return Component.empty();
        if (parts.size() == 1) return parts.get(0);
        Component result = parts.get(0);
        for (int i = 1; i < parts.size(); i++) {
            result = result.append(parts.get(i));
        }
        return result;
    }

    private static Component parseStandard(String input) {
        Matcher gradMatcher = GRADIENT_PATTERN.matcher(input);
        List<Component> parts = new ArrayList<>();
        int lastEnd = 0;

        while (gradMatcher.find()) {
            if (gradMatcher.start() > lastEnd) {
                parts.add(legacyToComponent(input.substring(lastEnd, gradMatcher.start())));
            }
            String color1 = gradMatcher.group(1);
            String color2 = gradMatcher.group(2);
            String text = gradMatcher.group(3);
            parts.add(createGradient(text, "#" + color1, "#" + color2));
            lastEnd = gradMatcher.end();
        }
        if (lastEnd < input.length()) {
            parts.add(legacyToComponent(input.substring(lastEnd)));
        }

        if (parts.isEmpty()) return Component.empty();
        if (parts.size() == 1) return parts.get(0);
        Component result = parts.get(0);
        for (int i = 1; i < parts.size(); i++) {
            result = result.append(parts.get(i));
        }
        return result;
    }

    private static Component createAnimatedGradient(String text, List<String> cycleColors) {
        if (cycleColors == null || cycleColors.size() < 2) {
            return createGradient(text, "#ff4444", "#44ff44");
        }
        int offset = (int) (animOffset % cycleColors.size());
        int idx1 = offset % cycleColors.size();
        int idx2 = (offset + 1) % cycleColors.size();
        return createGradient(text, cycleColors.get(idx1), cycleColors.get(idx2));
    }

    private static Component createGradient(String text, String hex1, String hex2) {
        TextColor color1 = TextColor.fromHexString(hex1);
        TextColor color2 = TextColor.fromHexString(hex2);
        if (color1 == null || color2 == null || text.isEmpty()) {
            return Component.text(text);
        }

        int r1 = color1.red(), g1 = color1.green(), b1 = color1.blue();
        int r2 = color2.red(), g2 = color2.green(), b2 = color2.blue();
        int len = text.length();

        TextComponent.Builder builder = Component.text();
        for (int i = 0; i < len; i++) {
            float ratio = len <= 1 ? 0.5f : (float) i / (len - 1);
            int r = Math.round(r1 + (r2 - r1) * ratio);
            int g = Math.round(g1 + (g2 - g1) * ratio);
            int b = Math.round(b1 + (b2 - b1) * ratio);
            builder.append(Component.text(String.valueOf(text.charAt(i))).color(TextColor.color(r, g, b)));
        }
        return builder.build();
    }

    public static String stripColor(String input) {
        return input.replaceAll("§[0-9a-fk-or]", "");
    }

    public static Component legacyToComponent(String input) {
        return LEGACY.deserialize(input);
    }
}
