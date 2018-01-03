package com.mobile.wanda.promoter.rest;

import android.util.Log;

/**
 * Created by kombo on 03/01/2018.
 */

public class ErrorHandler {

    public static String getExceptionMessage(RetrofitException retrofitException) {
        String message = null;

        try {
            RetrofitException.Kind kind = retrofitException.getKind();
            int code = retrofitException.getResponse() != null ? retrofitException.getResponse().code() : 0;
            String url = retrofitException.getUrl();
            String localizedMessage = retrofitException.getLocalizedMessage();


            switch (kind) {
                case HTTP:
                    if (code != 0) {
                        switch (code) {
                            case 400: //bad request
                                message = "Oops, something went wrong.";
                                break;
                            case 401: //unauthorized
                                message = "Sorry, access denied.";
                                break;
                            case 403: //forbidden
                                message = "Sorry, your request has been denied.";
                                break;
                            case 422: //unprocessable entity
                                message = "Malformed request";
                                break;
                            case 404: //not found
                                message = "Requested resource is unavailable";
                                break;
                            case 500: //internal server error
                                message = "An error occurred while parsing your request.";
                                break;
                            case 502: //bad gateway
                                message = "Server not responding.";
                                break;
                            case 503: //service unavailable
                                message = "The server could not process your request.";
                                break;
                            default:
                                message = "An unexpected error occurred. Please try again.";
                                break;
                        }
                    } else
                        message = "An unexpected error occurred. Please try again.";
                    break;
                case NETWORK:
                    message = "A network error occurred. Please try again.";
                    break;
                case UNEXPECTED:
                    message = "An unexpected error occurred. Please try again.";
                    break;
                default:
                    message = "An unexpected error occurred. Please try again";
                    break;
            }

            Log.e(ErrorHandler.class.getSimpleName(), message);
        } catch (Exception e) {
            Log.e(ErrorHandler.class.getSimpleName(), e.getLocalizedMessage(), e);
        }

        return message;
    }
}
