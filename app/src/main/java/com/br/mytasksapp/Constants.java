package com.br.mytasksapp;

public class Constants {
    public class API {

        public static final String CONTENT_TYPE = "application/json;charset=UTF-8";

        public static final String TYPE_REQUEST = "Bearer ";

        /* Homologação */
        private static final String BASE_APP = "http://192.168.1.2:3000";

        private static final String BASE_API = BASE_APP + "/";

        public static final String LOGIN = BASE_API + "auth/login";

        public static final String REGISTER = BASE_API + "auth/register";

        public static final String USERS = BASE_API + "users";

    }
}