package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import cz.gymtrebon.zaverecky.vjanecek.atlas.log.LogTyp;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(name = "log", schema = "config")
public class LoggerLine {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    private LogTyp typ;

    private String title;

    private String message;

    public LoggerLine(LogTyp typ, String user, String message) {
        this.typ = typ;
        if (user == null) {
            this.title = "";
        }else {
            this.title = user;
        }
        this.message = message;
    }
}