package net.vortexdata.netwatchdog.utils;

public class VersionUtils {

    public static boolean isVersionTagValid(String tag) {
        String[] split = tag.split("\\.");
        if (split.length != 3) return false;

        for (int i = 0; i < split.length; i++) {
            try {
                int parsed = Integer.parseInt(split[i]);
                if (parsed < 0) return false;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public static int compareVersionTags(String tag1, String tag2) {
        String[] level1 = tag1.split("\\.");
        String[] level2 = tag2.split("\\.");

        //get the larger length
        int largerLength = Math.max(level1.length, level2.length);

        //iterate over this larger length
        for (int i = 0; i < largerLength; i++) {
            Integer val1 = i < level1.length ? Integer.parseInt(level1[i]) : 0;
            Integer val2 = i < level2.length ? Integer.parseInt(level2[i]) : 0;

            Integer res = val1.compareTo(val2);

            if (res != 0) {
                return res;
            }

        }

        return 0;

    }

}
