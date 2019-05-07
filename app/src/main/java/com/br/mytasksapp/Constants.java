package com.br.mytasksapp;

public class Constants {
    public class API {

        public static final String CONTENT_TYPE = "application/json;charset=UTF-8";

        public static final String TYPE_REQUEST = "Bearer ";

        private static final String BASE_APP = IP.UFC;

        private static final String BASE_API = BASE_APP + "/";

        public static final String LOGIN = BASE_API + "auth/login";

        public static final String REGISTER = BASE_API + "auth/register";

        public static final String USER = BASE_API + "user";

        public static final String TASKS = USER + "/tasks";

        public static final String FORGOT_PASS = BASE_API + "auth/forgot_password";

        public static final String REFRESH_PASS = BASE_API + "auth/refresh_password/";

        public static final String FILES = BASE_API + "files/";

    }

    private class IP {
        private static final String UFC = "http://10.0.1.196:3000";

        private static final String MY_HOUSE_ARACOIABA = "http://192.168.0.104:3000";

        private static final String MY_HOUSE_QUIXADA = "http://192.168.1.3:3000";
    }
}