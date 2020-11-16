/*
 * Copyright (C) 2014-2015 Dominik Schürmann <dominik@dominikschuermann.de>
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

package org.openintents.openpgp.util;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import androidx.preference.ListPreference;
import android.util.AttributeSet;

import org.openintents.openpgp.R;

public class OpenPgpAppPreference extends ListPreference {
    private static final String PACKAGE_NAME_APG = "org.thialfihar.android.apg";
    private static final ArrayList<String> PROVIDER_BLACKLIST = new ArrayList<>();

    static {
        // Unfortunately, the current released version of APG includes a broken version of the API
        PROVIDER_BLACKLIST.add(PACKAGE_NAME_APG);
    }

    public OpenPgpAppPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        populateAppList();
    }

    public OpenPgpAppPreference(Context context) {
        this(context, null);
    }

    private void populateAppList() {
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        
        // add "none"-entry
        names.add(getContext().getString(R.string.openpgp_list_preference_none));
        values.add("");

        // search for OpenPGP providers...
        Intent intent = new Intent(OpenPgpApi.SERVICE_INTENT_2);
        List<ResolveInfo> resInfo = getContext().getPackageManager().queryIntentServices(intent, 0);
        if (resInfo != null) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (resolveInfo.serviceInfo == null) {
                    continue;
                }

                String packageName = resolveInfo.serviceInfo.packageName;
                
                if (!PROVIDER_BLACKLIST.contains(packageName)) {
                    names.add(String.valueOf(resolveInfo.serviceInfo.loadLabel(getContext().getPackageManager())));
                    values.add(packageName);
                }
            }
        }
        
        setEntries(names.toArray(new String[0]));
        setEntryValues(values.toArray(new String[0]));
    }
}
