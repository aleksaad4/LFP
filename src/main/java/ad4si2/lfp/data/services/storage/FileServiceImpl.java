package ad4si2.lfp.data.services.storage;

import ad4si2.lfp.data.entities.storage.LfpFile;
import ad4si2.lfp.data.repositories.storage.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
@Transactional
public class FileServiceImpl implements FileService {

    private static final int FILES_PER_DIRECTORY = 1000;

    @Nonnull
    @Value("${lfp.storage.path:/storage/}")
    private String storagePath;

    @Inject
    private FileRepository repository;

    @Nonnull
    @Override
    public FileRepository getRepo() {
        return repository;
    }

    @PostConstruct
    public void init() {
        // создадим промежуточные папки до хранилища
        final File f = new File(storagePath);
        if (!f.exists()) {
            // noinspection ResultOfMethodCallIgnored
            f.mkdirs();
        }
    }

    @Override
    @Nonnull
    public LfpFile create(@Nonnull final LfpFile file, final byte[] data) throws IOException {
        // проставим нужные поля
        file.setLength(data.length);
        file.setMd5(DigestUtils.md5DigestAsHex(data));

        // создаём запись о файле в базе
        final LfpFile created = create(file);

        try {
            // получаем file-obj куда необходимо записать данные
            final File f = getFileObject(created);
            // создаём промежуточные папки
            // noinspection ResultOfMethodCallIgnored
            f.getParentFile().mkdirs();

            // запишем содержимое в файл
            Files.write(f.toPath(), data);
        } catch (IOException e) {
            // помечаем файл удалённым, если его неудалось загрузить
            created.setDeleted(true);
            throw e;
        }

        return created;
    }

    @Override
    @Nonnull
    public byte[] getFileContent(@Nonnull final LfpFile file) throws IOException {
        final File f = getFileObject(file);
        return Files.readAllBytes(f.toPath());
    }

    @Override
    @Nonnull
    public File getFileObject(@Nonnull final LfpFile file) {
        return new File(storagePath, getRelativePath(file));
    }

    /**
     * Получение пути для сохранения файла
     *
     * @param file файл
     * @return путь до файла
     */
    @Nonnull
    private String getRelativePath(@Nonnull final LfpFile file) {
        final long id = file.getId();
        final long d1 = id / (FILES_PER_DIRECTORY * FILES_PER_DIRECTORY);
        final long d2 = (id % (FILES_PER_DIRECTORY * FILES_PER_DIRECTORY)) / FILES_PER_DIRECTORY;
        return String.format("%03d/%03d/%d-%s", d1, d2, id, file.getFileName());
    }
}
