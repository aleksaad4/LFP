package ad4si2.lfp.data.entities.storage;

import ad4si2.lfp.utils.data.IDeleted;
import ad4si2.lfp.utils.data.IEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lfp_file")
public class LfpFile implements IDeleted, IEntity<Long, LfpFile> {

    @Id
    @GeneratedValue
    private long id;

    @Nonnull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date loadDate = new Date();

    @Nonnull
    @Column(nullable = false)
    private String fileName;

    @Nonnull
    @Column(nullable = false)
    private String mimeType;

    @Column
    private long length;

    @Nullable
    @Column
    private String md5;

    @Column(nullable = false)
    private boolean deleted = false;

    @Nonnull
    @Enumerated
    private FileType type;

    public LfpFile() {
    }

    public LfpFile(@Nonnull final String fileName, @Nonnull final String mimeType, @Nonnull final FileType type) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.type = type;
    }

    public LfpFile(@Nonnull final LfpFile other) {
        this.id = other.id;
        this.loadDate = other.loadDate;
        this.fileName = other.fileName;
        this.mimeType = other.mimeType;
        this.length = other.length;
        this.md5 = other.md5;
        this.deleted = other.deleted;
        this.type = other.type;
    }

    @Nonnull
    public String getFileName() {
        return fileName;
    }

    @Nonnull
    @Override
    public Long getId() {
        return id;
    }

    public void setLength(final long length) {
        this.length = length;
    }

    public void setMd5(@Nullable final String md5) {
        this.md5 = md5;
    }

    @Nonnull
    @Override
    public LfpFile copy() {
        return new LfpFile(this);
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "LfpFile {" +
                "id=" + id +
                ", loadDate=" + loadDate +
                ", fileName='" + fileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", length=" + length +
                ", md5='" + md5 + '\'' +
                ", deleted=" + deleted +
                ", type=" + type +
                '}';
    }

    public enum FileType {
        IMAGE
    }
}
