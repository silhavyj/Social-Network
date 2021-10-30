package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.image;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        if (multipartFile == null)
            return false;
        String contentType = multipartFile.getContentType();
        return isSupportedContentType(contentType);
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}
