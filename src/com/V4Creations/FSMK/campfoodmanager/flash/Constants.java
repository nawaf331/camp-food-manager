/*
 * Copyright (C) 2010 beworx.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.V4Creations.FSMK.campfoodmanager.flash;

import android.os.Build;

public class Constants {

	public static final boolean DEBUG = false;
	public static final String TAG = "bwx.qs";
	
	public static final String PREFS_COMMON = "Common";
	public static final String PREFS_RUNTIME = "Runtime";

	public static final String PREF_FLASHLIGHT_TYPE = "flashlightType";

	public static final String ACTION_VOLUME_UPDATED = "com.bwx.bequick.VOLUME_UPDATED";
	public static final int SDK_VERSION = Integer.parseInt(Build.VERSION.SDK);
	
}
