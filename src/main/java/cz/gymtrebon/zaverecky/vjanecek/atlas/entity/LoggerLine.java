package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import cz.gymtrebon.zaverecky.vjanecek.atlas.log.LogTyp;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@Table(name = "log", schema = "config")
public class LoggerLine {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    private LogTyp typ;
    private String title;
    private String message;

    public LoggerLine(LogTyp typ, String user, String message) {
        this.typ = typ;
        this.title = Objects.requireNonNullElse(user, "");
        this.message = message;
    }
}