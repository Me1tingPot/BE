package meltingpot.server.util.r2;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${cloud.r2.bucket}")
    private String bucket;

    private final AmazonS3 cloudflareR2;

    public FileUploadResponse getPreSignedUrl(String prefix) {
        String fileId = createFileId();
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucket, createPath(prefix, fileId));
        URL url = cloudflareR2.generatePresignedUrl(generatePresignedUrlRequest);

        return FileUploadResponse.builder()
                .uploadUrl(url.toString())
                .fileKey(fileId)
                .build();
    }

    public void deleteFile(String prefix, String fileId) {
        cloudflareR2.deleteObject(bucket, createPath(prefix, fileId));
    }

    public String getCdnUrl(String prefix, String fileId) {
        return String.format("https://meltingpot-cdn.kaaa.ng/%s/%s", prefix, fileId);
    }

    private GeneratePresignedUrlRequest getGeneratePreSignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, fileName)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(getPreSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString());
        return generatePresignedUrlRequest;
    }

    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    private String createFileId() {
        return UUID.randomUUID().toString();
    }

    private String createPath(String prefix, String fileId) {
        if (fileId == null) {
            fileId = createFileId();
        }
        return String.format("%s/%s", prefix, fileId);
    }
}
