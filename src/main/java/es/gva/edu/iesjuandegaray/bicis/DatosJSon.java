package es.gva.edu.iesjuandegaray.bicis;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.json.*;

public class DatosJSon {
    private static String API_URL;
    private String datos = "";
    private String [] values;
    private int numEst;

    public DatosJSon(int nE) {
        numEst = nE;
        datos = "";
        API_URL =
            "https://geoportal.valencia.es/server/rest/services/OPENDATA/Trafico/MapServer/228/query"
            + "?where=1%3D1"
            + "&outFields=*"
            + "&returnGeometry=true"
            + "&f=json";
        values = new String [numEst];
        for (int i = 0; i < numEst; i++)
            values[i] = "";
    }

    public void mostrarDatos(int nE) {
        numEst = nE;
        datos = "";
        values = new String [numEst];
        for (int i = 0; i < numEst; i++)
            values[i] = "";

        try (var httpClient = HttpClients.createDefault()) {
            var req  = new HttpGet(API_URL);
            var resp = httpClient.execute(req);
            String json = EntityUtils.toString(resp.getEntity());

            JSONObject root     = new JSONObject(json);
            JSONArray  features = root.getJSONArray("features");
            int count = Math.min(numEst, features.length());

            for (int i = 0; i < count; i++) {
                JSONObject feat  = features.getJSONObject(i);
                JSONObject attrs = feat.getJSONObject("attributes");
                JSONObject geom  = feat.optJSONObject("geometry");

                String address = attrs.optString("DENOMINACION", "Desconocida");
                int available  = attrs.optInt("BICIS_DISPONIBLES", 0);
                int free       = attrs.optInt("PUESTOS_LIBRES", 0);
                int total      = attrs.optInt("TOTAL_PUESTOS", 0);
                double lon     = geom != null ? geom.optDouble("x", 0) : 0;
                double lat     = geom != null ? geom.optDouble("y", 0) : 0;

                datos += address
                    + " | Bicis: " + available
                    + " | Libres: " + free
                    + " | Total: " + total
                    + " | GPS: " + lat + ", " + lon + "\n";

                values[i] = String.format(
                    "INSERT IGNORE INTO estaciones(address,available,free,total,lat,lon) "
                    + "VALUES('%s',%d,%d,%d,%.6f,%.6f)",
                    address.replace("'","''"), available, free, total, lat, lon);
            }
        } catch (Exception e) {
            datos = "[ERROR] " + e.getMessage() + "\n";
        }
    }

    public String getDatos() {
        return datos;
    }

    public String[] getValues() {
        return values;
    }
}
