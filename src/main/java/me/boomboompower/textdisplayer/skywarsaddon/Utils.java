/*
 *     Copyright (C) 2017 boomboompower
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.boomboompower.textdisplayer.skywarsaddon;

import com.google.gson.JsonObject;

public class Utils {

    /* We are using Gson so these methods don't exist */

    public static String optString(JsonObject object, String key) {
        return optString(object, key, "");
    }

    public static String optString(JsonObject object, String key, String defaultValue) {
        return object.has(key) ? object.get(key).getAsString() : defaultValue;
    }

    public static Integer optInt(JsonObject object, String key) {
        return optInt(object, key, 0);
    }

    public static Integer optInt(JsonObject object, String key, int defaultValue) {
        return object.has(key) ? object.get(key).getAsInt() : defaultValue;
    }

    /* Make things easier */

    public static String remove(String input, String... toRemove) {
        for (String s : toRemove) {
            input = input.replace(s, "");
        }
        return input;
    }
}
