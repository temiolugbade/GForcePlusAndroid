package com.oymotion.newgforceprofiledemo;

import com.oymotion.gforceprofile.GForceProfile;

import java.io.Serializable;

public class GForceProfileSerializable implements Serializable {
        private GForceProfile gForceProfile;

        public GForceProfile getProfile() {
            return gForceProfile;
        }

        public void setProfile(GForceProfile gForceProfile) {
            this.gForceProfile = gForceProfile;
        }

}
