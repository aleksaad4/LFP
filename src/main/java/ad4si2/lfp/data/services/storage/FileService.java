package ad4si2.lfp.data.services.storage;

import ad4si2.lfp.data.entities.storage.LfpFile;
import ad4si2.lfp.data.repositories.storage.FileRepository;
import ad4si2.lfp.utils.data.ICRUDService;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

public interface FileService extends ICRUDService<LfpFile, Long, FileRepository> {

    /**
     * Загрузка файла: сохранение мета-информации в базе и загрузка файла на файловое хранилище
     *
     * @param file мета-информация о файле
     * @param data содержимое файла
     * @return созданный мета-файл
     * @throws IOException если не удалось сохранить файл в хранилище
     */
    @Nonnull
    LfpFile create(@Nonnull final LfpFile file, final byte[] data) throws IOException;

    /**
     * Получение содержимого файла
     *
     * @param file мета-информация о файле
     * @return содержимое
     * @throws IOException если не удалось прочитать файл
     */
    @Nonnull
    byte[] getFileContent(@Nonnull final LfpFile file) throws IOException;

    /**
     * Получение объекта File на файловом хранилище
     *
     * @param file мета-информация о файле
     * @return объект File
     */
    @Nonnull
    File getFileObject(@Nonnull final LfpFile file);
}
