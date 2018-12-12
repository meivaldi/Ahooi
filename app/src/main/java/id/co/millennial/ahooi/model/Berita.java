package id.co.millennial.ahooi.model;

/**
 * Created by root on 11/12/18.
 */

public class Berita {

    private String judul, url;

    public Berita(String judul) {
        this.judul = judul;
    }

    public Berita(String judul, String url) {
        this.judul = judul;
        this.url = url;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
