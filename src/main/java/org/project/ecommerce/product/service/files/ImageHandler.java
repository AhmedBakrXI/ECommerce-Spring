package org.project.ecommerce.product.service.files;

public interface ImageHandler extends ImageSaver, ImageRetriever {
    /**
     * Deletes an image from the storage.
     *
     * @param imagePath the path of the image to be deleted
     * @return true if the image was successfully deleted, false otherwise
     */
    boolean deleteImage(String imagePath);
}
