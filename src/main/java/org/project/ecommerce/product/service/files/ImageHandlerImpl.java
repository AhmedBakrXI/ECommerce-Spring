package org.project.ecommerce.product.service.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class ImageHandlerImpl implements ImageHandler {
    private final Logger logger = Logger.getLogger(ImageHandlerImpl.class.getName());

    @Value("${spring.application.ecommerce.image-directory}")
    private String imageDir;

    /**
     * Saves an image to the specified directory with a unique name.
     *
     * @param imageName the name of the image
     * @param file      the image file to be saved
     * @return the path of the saved image, or null if saving failed
     */
    @Override
    public String saveImage(String imageName, MultipartFile file) {
        String fileType = file.getContentType();
        if (!"image/png".equalsIgnoreCase(fileType) &&
                !"image/jpeg".equalsIgnoreCase(fileType)) {
            logger.warning("Invalid image type: " + fileType);
            return null;
        }
        String relativePath = UUID.randomUUID() + "_" + imageName + "." + fileType.split("/")[1];
        String imagePath = imageDir + relativePath;
        File imageFile = new File(imagePath);
        try {
            // Ensure the directory exists
            File dir = new File(imageDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file.transferTo(imageFile);
            // Return relative path for the image
            return relativePath;
        } catch (Exception e) {
            logger.warning(e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the URL of an image based on its name.
     *
     * @param imageName the name of the image
     * @return the URL of the image
     */
    @Override
    public String getImageUrl(String imageName) {
        File imageFile = new File(imageDir + imageName);
        if (imageFile.exists()) {
            return imageFile.getAbsolutePath();
        }
        return "";
    }

    /**
     * Retrieves the image resource based on its name.
     *
     * @param imageName the name of the image
     * @return the image resource
     */
    @Override
    public Resource getImageResource(String imageName) {
        String filePath = getImageUrl(imageName);
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if (file.exists()) {
                return new FileSystemResource(file);
            } else {
                logger.warning("Image file does not exist: " + filePath);
            }
        }
        return null;
    }

    /**
     * Deletes an image from the storage.
     *
     * @param imagePath the path of the image to be deleted
     * @return true if the image was successfully deleted, false otherwise
     */
    @Override
    public boolean deleteImage(String imagePath) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            if (imageFile.delete()) {
                logger.info("Image deleted successfully: " + imagePath);
                return true;
            } else {
                logger.warning("Failed to delete image: " + imagePath);
            }
        } else {
            logger.warning("Image file does not exist: " + imagePath);
        }
        return false;
    }
}
