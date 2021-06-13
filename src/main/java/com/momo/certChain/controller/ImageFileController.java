package com.momo.certChain.controller;
import com.momo.certChain.services.ImageFileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
public class ImageFileController extends BaseController {

    private final ImageFileService imageFileService;

    public ImageFileController(ImageFileService imageFileService) {
        this.imageFileService = imageFileService;
    }

    @GetMapping("/{id}")
    public byte[] getImageFile(@PathVariable String id){
        return imageFileService.findImageFile(id).getBytes();
    }
}
