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

import org.mariotaku.twidere.api.twitter.model.TranslationResult;

/**
 * Created by mariotaku on 15/5/7.
 */
@JsonObject
public class TranslationResultImpl extends TwitterResponseImpl implements TranslationResult {

    @JsonField(name = "id")
    long id;
    @JsonField(name = "lang")
    String lang;
    @JsonField(name = "translated_lang")
    String translatedLang;
    @JsonField(name = "translation_type")
    String translationType;
    @JsonField(name = "text")
    String text;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getLang() {
        return lang;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getTranslatedLang() {
        return translatedLang;
    }

    @Override
    public String getTranslationType() {
        return translationType;
    }
}
