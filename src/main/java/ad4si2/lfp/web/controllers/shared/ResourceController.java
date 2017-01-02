package ad4si2.lfp.web.controllers.shared;

import ad4si2.lfp.data.entities.storage.LfpFile;
import ad4si2.lfp.data.services.storage.FileService;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.utils.web.WebUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;

@RestController
@RequestMapping("/rest/account/")
public class ResourceController {

    @Inject
    private FileService fileService;

    @Inject
    private WebUtils webUtils;

    @RequestMapping(value = "loadImage", method = RequestMethod.POST)
    public AjaxResponse loadImage(@RequestBody final MultipartFile file) {
        // проверяем, что файл не пуст
        if (file == null || file.isEmpty()) {
            return webUtils.errorResponse(EntityValidatorResult.validatorResult("File absent", "common.file_absent"));
        }

        // проверяем, что файл картинка
        if (!file.getContentType().startsWith("image")) {
            return webUtils.errorResponse(EntityValidatorResult.validatorResult("File is not image", "common.file_is_not_image"));
        }

        try {
            // загружаем файл
            final LfpFile created = fileService.create(new LfpFile(file.getName(), file.getContentType(), LfpFile.FileType.IMAGE), file.getBytes());
            created.setUrl("/rest/account/file/" + created.getId() + "/");
            return webUtils.successResponse(created);
        } catch (IOException e) {
            // неудалось загрузить файл
            return webUtils.errorResponse(EntityValidatorResult.validatorResult("Can't load file", "common.file_can_t_load"));
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/file/{fileId}")
    public byte[] getImage(@PathVariable(value = "fileId") final Long fileId) throws IOException {
        final LfpFile file = fileService.getById(fileId, false);
        return fileService.getFileContent(file);
    }
}
