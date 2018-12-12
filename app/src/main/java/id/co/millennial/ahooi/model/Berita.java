package id.co.millennial.ahooi.model;

/**
 * Created by root on 11/12/18.
 */

public class Berita {

    private String judul, url;
    private boolean clickable;

    public Berita(String judul, boolean clickable) {
        this.judul = judul;
        this.clickable = clickable;
    }

    public Berita(String judul, String url, boolean clickable) {
        this.judul = judul;
        this.url = url;
        this.clickable = clickable;
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

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }
}
