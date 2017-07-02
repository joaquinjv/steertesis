/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.unlp.tesis.steer;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

/**
 * Constants used in this sample.
 */
public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 500;

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> LAPLATA_GEOFENCES_AREAS = new HashMap<String, LatLng>();
    static {
        // San Francisco International Airport.
        LAPLATA_GEOFENCES_AREAS.put("PEDRO", new LatLng(-34.942166,-57.9655665));

        // Googleplex.
        LAPLATA_GEOFENCES_AREAS.put("JOACO", new LatLng(-34.9306643,-57.9727699));
    }

    /**
     * Constants for operations to do in the server
     */
    public static final String E_START_PARKING = "iniciarEstacionamiento";
    public static final String E_END_PARKING = "finalizarEstacionamiento";
    public static final String E_STATE_OF_PARKING= "consultarEstado";

    /**
     * Constants for text recognized in the speech voice
     */
    public static final String[] E_SPEECH_TO_START_PARKING = new String[]{
            "estacionar","iniciar estacionamiento"};
    public static final String[] E_SPEECH_TO_END_PARKING = new String[]{
            "fin","finalizar","finalizar estacionamiento", "terminar"};
    public static final String[] E_SPEECH_TO_STATE_PARKING = new String[]{
            "saldo","estado", "consulta"};
}
