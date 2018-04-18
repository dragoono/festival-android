package devnik.trancefestivalticker.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by nik on 06.03.2018.
 */
@Entity
public class WhatsNew {
    @Id
    private Long id;
    private String item;
    private String festivalName;
    @Generated(hash = 1170987171)
    public WhatsNew(Long id, String item, String festivalName) {
        this.id = id;
        this.item = item;
        this.festivalName = festivalName;
    }
    @Generated(hash = 596195766)
    public WhatsNew() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getItem() {
        return this.item;
    }
    public void setItem(String item) {
        this.item = item;
    }
    public String getFestivalName() {
        return this.festivalName;
    }
    public void setFestivalName(String festivalName) {
        this.festivalName = festivalName;
    }
    
}
