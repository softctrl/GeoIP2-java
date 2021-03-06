package com.maxmind.geoip2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

import org.junit.Test;

import com.google.api.client.http.HttpTransport;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityIspOrgResponse;

public class NamesTest {
    HttpTransport transport = new TestTransport();

    @Test
    public void testNames() throws IOException, GeoIp2Exception {
        WebServiceClient client = new WebServiceClient.Builder(42,
                "abcdef123456").transport(this.transport)
                .locales(Arrays.asList("zh-CN", "ru")).build();

        CityIspOrgResponse cio = client.cityIspOrg(InetAddress
                .getByName("1.1.1.2"));
        assertEquals("country.getContinent().getName() does not return 北美洲",
                "北美洲", cio.getContinent().getName());
        assertEquals("country.getCountry().getName() does not return 美国", "美国",
                cio.getCountry().getName());
        assertEquals("toString() returns getName()",
                cio.getCountry().getName(), cio.getCountry().getName());
    }

    @Test
    public void russianFallback() throws IOException, GeoIp2Exception {
        WebServiceClient client = new WebServiceClient.Builder(42,
                "abcdef123456").transport(this.transport)
                .locales(Arrays.asList("as", "ru")).build();

        CityIspOrgResponse cio = client.cityIspOrg(InetAddress
                .getByName("1.1.1.2"));
        assertEquals(
                "country.getCountry().getName() does not return объединяет государства",
                "объединяет государства", cio.getCountry().getName());

    }

    @Test
    public void testFallback() throws IOException, GeoIp2Exception {
        WebServiceClient client = new WebServiceClient.Builder(42,
                "abcdef123456").transport(this.transport)
                .locales(Arrays.asList("pt", "en", "zh-CN")).build();
        CityIspOrgResponse cio = client.cityIspOrg(InetAddress
                .getByName("1.1.1.2"));
        assertEquals("en is returned when pt is missing", cio.getContinent()
                .getName(), "North America");

    }

    @Test
    public void noFallback() throws IOException, GeoIp2Exception {
        WebServiceClient client = new WebServiceClient.Builder(42,
                "abcdef123456").transport(this.transport)
                .locales(Arrays.asList("pt", "es", "af")).build();
        CityIspOrgResponse cio = client.cityIspOrg(InetAddress
                .getByName("1.1.1.2"));

        assertNull("null is returned when locale is not available", cio
                .getContinent().getName());
    }

    @Test
    public void noLocale() throws IOException, GeoIp2Exception {
        WebServiceClient client = new WebServiceClient.Builder(42,
                "abcdef123456").transport(this.transport).build();
        CityIspOrgResponse cio = client.cityIspOrg(InetAddress
                .getByName("1.1.1.2"));
        assertEquals("en is returned when no locales are specified", cio
                .getContinent().getName(), "North America");

    }

    @Test
    public void testMissing() throws IOException, GeoIp2Exception {
        WebServiceClient client = new WebServiceClient.Builder(42,
                "abcdef123456").transport(this.transport)
                .locales(Arrays.asList("en")).build();

        CityIspOrgResponse cio = client.cityIspOrg(InetAddress
                .getByName("1.1.1.2"));
        assertNotNull(cio.getCity());
        assertNull("null is returned when names object is missing", cio
                .getCity().getName());
    }
}
