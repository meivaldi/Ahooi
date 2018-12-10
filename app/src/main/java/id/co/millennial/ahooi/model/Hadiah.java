package id.co.millennial.ahooi.model;

/**
 * Created by root on 08/12/18.
 */

public class Hadiah {

    private String id, nama, point, foto;

    public Hadiah(String id, String nama, String point, String foto) {
        this.id = id;
        this.nama = nama;
        this.point = point;
        this.foto = foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
