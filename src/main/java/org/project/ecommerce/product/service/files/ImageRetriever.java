package org.project.ecommerce.product.service.files;

import org.springframework.core.io.Resource;

public interface ImageRetriever {
    /**
     * Retrieves the URL of an image based on its name.
     *
     * @param imageName the name of the image
     * @return the URL of the image
     */
    String getImageUrl(String imageName);

    /**
     * Retrieves the image resource based on its name.
     *
     * @param imageName the name of the image
     * @return the image resource
     */
    Resource getImageResource(String imageName);
}
