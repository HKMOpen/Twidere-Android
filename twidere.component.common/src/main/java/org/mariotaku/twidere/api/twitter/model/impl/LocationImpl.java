/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.api.twitter.model.impl;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.mariotaku.twidere.api.twitter.model.Location;

/**
 * Created by mariotaku on 15/5/10.
 */
@JsonObject
public class LocationImpl implements Location {

    @JsonField(name = "woeid")
    int woeid;
    @JsonField(name = "country")
    String countryName;
    @JsonField(name = "countryCode")
    String countryCode;
    @JsonField(name = "placeType")
    PlaceTypeImpl placeType;
    @JsonField(name = "name")
    String name;
    @JsonField(name = "url")
    String url;

    @Override
    public int getWoeid() {
        return woeid;
    }

    @Override
    public String getCountryName() {
        return countryName;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public PlaceTypeImpl getPlaceType() {

        return placeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @JsonObject
    public static class PlaceTypeImpl implements PlaceType {

        @JsonField(name = "name")
        String name;
        @JsonField(name = "code")
        int code;

        @Override
        public int getCode() {
            return code;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}