package org.project.ecommerce.product.service.files;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageSaver {
    /**
     * Saves an image to the specified directory with a unique name.
     *
     * @param imageName the name of the image
     * @param file      the image file to be saved
     * @return the path of the saved image, or null if saving failed
     */
    String saveImage(String imageName, MultipartFile file) throws IOException;
}
