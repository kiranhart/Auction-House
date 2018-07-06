package com.shadebyte.auctionhouse.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Assists in the creation of multiple localizations and languages,
 * as well as the generation of default .lang files
 *
 * @author Parker Hawke - 2008Choco
 */
public class Locale {

    private static JavaPlugin plugin;
    private static final List<Locale> LOCALES = Lists.newArrayList();

    private static final Pattern NODE_PATTERN = Pattern.compile("(\\w+(?:\\.\\w+)*)\\s*=\\s*\"(.*)\"");
    private static final String FILE_EXTENSION = ".lang";
    private static File localeFolder;

    private static String defaultLocale;

    private final Map<String, String> nodes = new HashMap<>();

    private final File file;
    private final String name, region;

    private Locale(String name, String region) {
        if (plugin == null)
            throw new IllegalStateException("Cannot generate locales without first initializing the class (Locale#init(JavaPlugin))");

        this.name = name.toLowerCase();
        this.region = region.toUpperCase();

        String fileName = name + "_" + region + FILE_EXTENSION;
        this.file = new File(localeFolder, fileName);

        if (this.reloadMessages()) return;

        plugin.getLogger().info("Loaded locale " + fileName);
    }

    /**
     * Get the name of the language that this locale is based on.
     * (i.e. "en" for English, or "fr" for French)
     *
     * @return the name of the language
     */
    public String getName() {
        return name;
    }

    /**
     * Get the name of the region that this locale is from.
     * (i.e. "US" for United States or "CA" for Canada)
     *
     * @return the name of the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * Return the entire locale tag (i.e. "en_US")
     *
     * @return the language tag
     */
    public String getLanguageTag() {
        return name + "_" + region;
    }

    /**
     * Get the file that represents this locale
     *
     * @return the locale file (.lang)
     */
    public File getFile() {
        return file;
    }

    /**
     * Get a message set for a specific node
     *
     * @param node the node to get
     * @return the message for the specified node
     */
    public String getMessage(String node) {
        return ChatColor.translateAlternateColorCodes('&', this.getMessageOrDefault(node, node));
    }

    /**
     * Get a message set for a specific node and replace its params with a supplied arguments.
     *
     * @param node the node to get
     * @param args the replacement arguments
     * @return the message for the specified node
     */
    public String getMessage(String node, Object... args) {
        String message = getMessage(node);
        for (Object arg : args) {
            message = message.replaceFirst("%.*?%", arg.toString());
        }
        return message;
    }

    /**
     * Get a message set for a specific node
     *
     * @param node         the node to get
     * @param defaultValue the default value given that a value for the node was not found
     * @return the message for the specified node. Default if none found
     */
    public String getMessageOrDefault(String node, String defaultValue) {
        return this.nodes.getOrDefault(node, defaultValue);
    }

    /**
     * Get the key-value map of nodes to messages
     *
     * @return node-message map
     */
    public Map<String, String> getMessageNodeMap() {
        return ImmutableMap.copyOf(nodes);
    }

    /**
     * Clear the previous message cache and load new messages directly from file
     *
     * @return reload messages from file
     */
    public boolean reloadMessages() {
        if (!this.file.exists()) {
            plugin.getLogger().warning("Could not find file for locale " + this.name);
            return false;
        }

        this.nodes.clear(); // Clear previous data (if any)

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            for (int lineNumber = 0; (line = reader.readLine()) != null; lineNumber++) {
                if (line.isEmpty() || line.startsWith("#") /* Comment */) continue;

                Matcher matcher = NODE_PATTERN.matcher(line);
                if (!matcher.find()) {
                    System.err.println("Invalid locale syntax at (line=" + lineNumber + ")");
                    continue;
                }

                nodes.put(matcher.group(1), matcher.group(2));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Initialize the locale class to generate information and search for localizations.
     * This must be called before any other methods in the Locale class can be invoked.
     * Note that this will also call {@link #searchForLocales()}, so there is no need to
     * invoke it for yourself after the initialization
     *
     * @param plugin the plugin instance
     */
    public static void init(JavaPlugin plugin) {
        Locale.plugin = plugin;

        if (localeFolder == null) {
            localeFolder = new File(plugin.getDataFolder(), "locales/");
        }

        localeFolder.mkdirs();
        Locale.searchForLocales();
    }

    /**
     * Find all .lang file locales under the "locales" folder
     */
    public static void searchForLocales() {
        if (!localeFolder.exists()) localeFolder.mkdirs();

        for (File file : localeFolder.listFiles()) {
            String name = file.getName();
            if (!name.endsWith(".lang")) continue;

            String fileName = name.substring(0, name.lastIndexOf('.'));
            String[] localeValues = fileName.split("_");

            if (localeValues.length != 2) continue;
            if (localeExists(localeValues[0] + "_" + localeValues[1])) continue;

            LOCALES.add(new Locale(localeValues[0], localeValues[1]));
            plugin.getLogger().info("Found and loaded locale \"" + fileName + "\"");
        }
    }

    /**
     * Get a locale by its entire proper name (i.e. "en_US")
     *
     * @param name the full name of the locale
     * @return locale of the specified name
     */
    public static Locale getLocale(String name) {
        for (Locale locale : LOCALES)
            if (locale.getLanguageTag().equalsIgnoreCase(name)) return locale;
        return null;
    }

    /**
     * Get a locale from the cache by its name (i.e. "en" from "en_US")
     *
     * @param name the name of the language
     * @return locale of the specified language. Null if not cached
     */
    public static Locale getLocaleByName(String name) {
        for (Locale locale : LOCALES)
            if (locale.getName().equalsIgnoreCase(name)) return locale;
        return null;
    }

    /**
     * Get a locale from the cache by its region (i.e. "US" from "en_US")
     *
     * @param region the name of the region
     * @return locale of the specified region. Null if not cached
     */
    public static Locale getLocaleByRegion(String region) {
        for (Locale locale : LOCALES)
            if (locale.getRegion().equalsIgnoreCase(region)) return locale;
        return null;
    }

    /**
     * Check whether a locale exists and is registered or not
     *
     * @param name the whole language tag (i.e. "en_US")
     * @return true if it exists
     */
    public static boolean localeExists(String name) {
        for (Locale locale : LOCALES)
            if (locale.getLanguageTag().equals(name)) return true;
        return false;
    }

    /**
     * Get an immutable list of all currently loaded locales
     *
     * @return list of all locales
     */
    public static List<Locale> getLocales() {
        return ImmutableList.copyOf(LOCALES);
    }

    /**
     * Save a default locale file from the project source directory, to the locale folder
     *
     * @param path     the path to the file to save
     * @param fileName the name of the file to save
     * @return true if the operation was successful, false otherwise
     */
    public static boolean saveDefaultLocale(String path, String fileName) {
        if (!localeFolder.exists()) localeFolder.mkdirs();

        if (!fileName.endsWith(FILE_EXTENSION))
            fileName = (fileName.lastIndexOf(".") == -1 ? fileName : fileName.substring(0, fileName.lastIndexOf('.'))) + FILE_EXTENSION;

        File destinationFile = new File(localeFolder, fileName);
        if (destinationFile.exists()) {
            return compareFiles(plugin.getResource(fileName), destinationFile);
        }

        try (OutputStream outputStream = new FileOutputStream(destinationFile)) {
            IOUtils.copy(plugin.getResource(fileName), outputStream);

            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            String[] localeValues = fileName.split("_");

            if (localeValues.length != 2) return false;

            LOCALES.add(new Locale(localeValues[0], localeValues[1]));
            if (defaultLocale == null) defaultLocale = fileName;

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Save a default locale file from the project source directory, to the locale folder
     *
     * @param fileName the name of the file to save
     * @return true if the operation was successful, false otherwise
     */
    public static boolean saveDefaultLocale(String fileName) {
        return saveDefaultLocale("", fileName);
    }

    /**
     * Clear all current locale data
     */
    public static void clearLocaleData() {
        for (Locale locale : LOCALES)
            locale.nodes.clear();
        LOCALES.clear();
    }

    // Write new changes to existing files, if any at all
    private static boolean compareFiles(InputStream defaultFile, File existingFile) {
        // Look for default
        if (defaultFile == null) {
            defaultFile = plugin.getResource(defaultLocale != null ? defaultLocale : "en_US");
            if (defaultFile == null) return false; // No default at all
        }

        boolean changed = false;

        List<String> defaultLines, existingLines;
        try (BufferedReader defaultReader = new BufferedReader(new InputStreamReader(defaultFile));
             BufferedReader existingReader = new BufferedReader(new FileReader(existingFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(existingFile, true))) {
            defaultLines = defaultReader.lines().collect(Collectors.toList());
            existingLines = existingReader.lines().map(s -> s.split("\\s*=")[0]).collect(Collectors.toList());

            for (String defaultValue : defaultLines) {
                if (defaultValue.isEmpty() || defaultValue.startsWith("#")) continue;

                String key = defaultValue.split("\\s*=")[0];

                if (!existingLines.contains(key)) {
                    if (!changed) {
                        writer.newLine();
                        writer.newLine();
                        writer.write("# New messages for " + plugin.getName() + " v" + plugin.getDescription().getVersion());
                    }

                    writer.newLine();
                    writer.write(defaultValue);

                    changed = true;
                }
            }
        } catch (IOException e) {
            return false;
        }

        return changed;
    }

}