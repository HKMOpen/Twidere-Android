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

package org.mariotaku.twidere.view.holder;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import org.mariotaku.twidere.fragment.support.AccountsDashboardFragment;
import org.mariotaku.twidere.util.MediaLoadingHandler;
import org.mariotaku.twidere.view.ProfileBannerImageView;
import org.mariotaku.twidere.view.ProfileImageView;
import org.mariotaku.twidere.view.ShapedImageView;

/**
 * Created by mariotaku on 15/6/8.
 */
public class loader {
    public static void displayProfileImage(final ProfileImageView profileImageView, final String quoted_by_user_profile_image) {
        profileImageView.setImageURI(Uri.parse(quoted_by_user_profile_image));
    }

    public static void displayProfileImage(final ImageView profileImageView, final String quoted_by_user_profile_image) {
    }

    public static void cancelDisplayTask(final ProfileImageView profileImageView) {
        profileImageView.setImageURI(null);
    }

    public static void displayPreviewImage(final String uri, final ImageView image) {

    }

    public static void displayPreviewImageWithCredentials(final ImageView imageView, final String url, final long accountId, final MediaLoadingHandler loadingHandler) {

    }

    public static void displayPreviewImage(final ImageView imageView, final String url, final MediaLoadingHandler loadingHandler) {

    }

    public static void cancelDisplayTask(final ImageView imageView) {

    }

    public static void displayDashboardProfileImage(final ShapedImageView clickedImageView, final String profile_image_url, final Drawable profileDrawable) {

    }

    public static void displayProfileBanner(final ImageView mAccountProfileBannerView, final String bannerUrl, final AccountsDashboardFragment accountsDashboardFragment) {

    }

    public static void displayProfileBanner(final ImageView mProfileBannerView, final String profile_banner_url) {

    }
}
