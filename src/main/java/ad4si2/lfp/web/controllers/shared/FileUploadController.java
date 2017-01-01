package ad4si2.lfp.web.controllers.shared;

import ad4si2.lfp.data.entities.storage.LfpFile;
import ad4si2.lfp.data.services.storage.FileService;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.utils.web.WebUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;

@RestController
@RequestMapping("/rest/account/")
public class FileUploadController {

    @Inject
    private FileService fileService;

    @Inject
    private WebUtils webUtils;

    @RequestMapping(value = "loadImage")
    public AjaxResponse loadImage(@RequestParam("file") final MultipartFile file) {
        // проверяем, что файл не пуст
        if (file == null || file.isEmpty()) {
            return webUtils.errorResponse(EntityValidatorResult.validatorResult("common.file_absent", "File absent"));
        }

        try {
            // загружаем файл
            final LfpFile created = fileService.create(new LfpFile(file.getName(), file.getContentType(), LfpFile.FileType.IMAGE), file.getBytes());
            return webUtils.successResponse(created);
        } catch (IOException e) {
            // неудалось загрузить файл
            return webUtils.errorResponse(EntityValidatorResult.validatorResult("common.file_can_t_load", "Can't load file"));
        }
    }

}
