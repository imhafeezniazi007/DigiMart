package com.example.digimart.Utils;

public class Consts {
        public static String API_BASE_URL = "BASE_URL";
        public static String GET_CATEGORIES_URL = API_BASE_URL + "/product/getAllCategory";
        public static String GET_PRODUCTS_URL = API_BASE_URL + "/product";
        public static String GET_PRODUCT_DETAILS_URL = API_BASE_URL + "/services/getProductDetails?id=";
        public static String POST_ORDER_URL = API_BASE_URL + "/services/submitProductOrder";
        public static String PAYMENT_URL = API_BASE_URL + "/services/paymentPage?code=";

        public static String CATEGORIES_IMAGE_URL = API_BASE_URL + "/uploads/category/";
        public static String PRODUCTS_IMAGE_URL = API_BASE_URL + "/uploads/product/";
    }
