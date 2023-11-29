package com.example.dietcommunity.common.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.PostException;
import com.example.dietcommunity.post.entity.PostImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ImageService {

  private static final List<String> SUPPORTED_IMAGE_EXTENSION = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");

  private final AmazonS3Client amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String s3BucketName;


  // s3버킷에 저장하면서 생성한 이미지 url 리스트를 반환
  public List<String> uploadImages(List<MultipartFile> multipartFiles, long postId) {

    if (multipartFiles.size() > 5) {
      throw new IllegalArgumentException("이미지는 5개까지 저장가능합니다.");
    }
    List<String> imageUrls = new ArrayList<>();

    for (MultipartFile file : multipartFiles) {

      validateImageFile(file);
      String fileName = createFileName(file, postId);  // s3에 저장되는 파일은 모두 구분되어야하고, 동일한 이름의 파일들이 버킷에 들어갈 수도 있으니 별도의 고유이름으로 만들어줘야함

      // s3에 저장
      try {
        amazonS3Client.putObject(
            new PutObjectRequest(s3BucketName, fileName, file.getInputStream(), getObjectMetadata(file))
                .withCannedAcl(CannedAccessControlList.PublicRead));
        log.info("s3버킷에 이미지 저장성공 : {}", fileName);

        imageUrls.add(amazonS3Client.getUrl(s3BucketName, fileName).toString());

      } catch (IOException e) {
        log.error("이미지 업로드에 실패했습니다.", e);
      }
    }

    return imageUrls;
  }

  private void deleteImage(String imageUrl){

    String imageKey = getImageKey(imageUrl);

    if(!amazonS3Client.doesObjectExist(s3BucketName, imageKey)){
      log.error("이미지 삭제실패 [버킷에 존재하지 않는 이미지] : {}", imageKey);
    }else {
      amazonS3Client.deleteObject(s3BucketName, imageKey);
    }
  }

  public void deleteAllImages(List<PostImage> existingImages){
    existingImages.stream()
        .filter(Objects::nonNull)
        .forEach(image -> deleteImage(image.getImageUrl()));
  }

  private String getImageKey(String imageUrl){
    return imageUrl.substring(imageUrl.indexOf("com/") + 3);
  }


  // s3에 저장할 고유의 객체 이름 생성
  private String createFileName(MultipartFile file, long postId) {

    return UUID.randomUUID() + "-" + postId;
  }


  private ObjectMetadata getObjectMetadata(MultipartFile file) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(file.getContentType()); // MultipartFile 객체로부터 MIME 타입을 얻어옴
    objectMetadata.setContentLength(file.getSize());
    return objectMetadata;
  }


  private void validateImageFile(MultipartFile file) {

    if (file == null || file.isEmpty()) {
      throw new NullPointerException("file이 존재하지 않습니다.");
    }
    String fileName = file.getOriginalFilename();

    if (fileName == null || fileName.length() < 4) {
      throw new IllegalArgumentException(fileName + " :잘못된 형식의 파일입니다.");
    }

    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

    if (!SUPPORTED_IMAGE_EXTENSION.contains(fileExtension)) {
      throw new IllegalArgumentException(fileExtension + " 는 지원하지 않는 형식의 확장자입니다.");
    }

  }

}
