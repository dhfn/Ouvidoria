package br.ufrr.promobile.ufrrmobile.ouvidoria;

/**
 * Created by promobile on 21/10/15.
 */
public class Util {
    // TIME AGO
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time <= 0) {
            return null;
        }
        else if( time > now ){
            time = now;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "agora";
        }
        else if (diff < 2 * MINUTE_MILLIS) {
            return "a um minuto";
        }
        else if (diff < 50 * MINUTE_MILLIS) {
            return "a "+(diff / MINUTE_MILLIS) + " minutos";
        }
        else if (diff < 90 * MINUTE_MILLIS) {
            return "a uma hora";
        }
        else if (diff < 24 * HOUR_MILLIS) {
            return "a "+(diff / HOUR_MILLIS) + " horas";
        }
        else if (diff < 48 * HOUR_MILLIS) {
            return "ontem";
        }
        else {
            return "a "+(diff / DAY_MILLIS) + " dias";
        }
    }

}
