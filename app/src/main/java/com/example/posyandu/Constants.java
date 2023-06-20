package com.example.posyandu;

public class Constants {
    private static final String ROOT_URL = "http://192.168.1.23/posyandu/v1/";

    public static  final String URL_REGISTER = ROOT_URL + "registerUser.php";
    public static  final String URL_LOGIN = ROOT_URL + "userLogin.php";
    public static  final String URL_GET_ANTRIAN = ROOT_URL + "getAntrian.php";

    public static  final String URL_ADD_ANTRIAN = ROOT_URL + "addAntrian.php";
    public static  final String URL_BATALKAN_ANTRIAN = ROOT_URL + "batalkanAntrian.php";
    public static  final String URL_SELESAIKAN_ANTRIAN = ROOT_URL + "selesaikanAntrian.php";
    public static  final String URL_PENDINGKAN_ANTRIAN = ROOT_URL + "pendingkanAntrian.php";
}
