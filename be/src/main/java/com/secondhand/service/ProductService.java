package com.secondhand.service;

import com.secondhand.domain.categoriy.Category;
import com.secondhand.exception.BadRequestException;
import com.secondhand.exception.v2.ErrorMessage;
import com.secondhand.exception.NotUserMineProductException;
import com.secondhand.exception.ProductNotFoundException;
import com.secondhand.domain.image.Image;
import com.secondhand.domain.product.repository.Image.ImageRepository;
import com.secondhand.domain.interested.Interested;
import com.secondhand.domain.interested.InterestedRepository;
import com.secondhand.domain.member.Member;
import com.secondhand.domain.product.Product;
import com.secondhand.domain.product.repository.ProductRepository;
import com.secondhand.domain.town.Town;
import com.secondhand.web.dto.requset.ProductSaveRequest;
import com.secondhand.web.dto.requset.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int IMAGE_LIST_MAX_SIZE = 9;

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final InterestedRepository interestedRepository;
    private final CategoryService categoryService;
    private final TownService townService;
    private final MemberService memberService;
    private final ImageService imageService;

    @Transactional
    public Long save(long userId, ProductSaveRequest requestInfo, MultipartFile thumbnailImage, List<MultipartFile> images) {
        if (!isValidThumbnail(thumbnailImage)) {
            throw new BadRequestException(ErrorMessage.INVALID_REQUEST, "썸네일 이미지는 반드시 들어와야 합니다.");
        }
        if (images != null && images.size() > IMAGE_LIST_MAX_SIZE) {
            throw new BadRequestException(ErrorMessage.INVALID_REQUEST, "썸네일 이미지 외의 이미지는 최대 9개까지 들어올 수 있습니다.");
        }

        String thumbnailUrl = imageService.upload(thumbnailImage);
        List<String> itemImageUrls = imageService.uploadImageList(images);

        itemImageUrls = new ArrayList<>(itemImageUrls);
        itemImageUrls.add(thumbnailUrl);
        log.debug("requestInfo = {}", requestInfo);
        Category category = categoryService.findById(requestInfo.getCategoryId());
        log.debug("category = {}", category);
        Town town = townService.findById(requestInfo.getTownId());
        log.debug("town = {}", town);
        Member member = memberService.findMemberById(userId);
        log.debug("member = {}", member);
        Product product = Product.create(requestInfo, member, category, town, thumbnailUrl);
        log.debug("product = {}", product);
        Product saveProduct = productRepository.save(product);
        log.debug("saveProduct = {}", saveProduct);

        List<Image> itemImages = itemImageUrls.stream()
                .map(url -> Image.of(url, saveProduct))
                .collect(Collectors.toList());
        imageRepository.saveAllImages(itemImages);

        log.debug("itemImages = {}", itemImages);
        return saveProduct.getId();
    }

    private boolean isValidThumbnail(MultipartFile image) {
        return image != null && !image.isEmpty();
    }

    @Transactional
    public void update(long productId, ProductUpdateRequest updateRequest, long userId) {
        Category category = categoryService.findById(updateRequest.getCategoryId());
        Town town = townService.findById(updateRequest.getTownId());
        Product product = findById(productId);
        checkIsMine(userId, product.getMember().getId());
        product.update(updateRequest, category, town);
        log.debug("product = {}", product);
    }

    @Transactional
    public void delete(long userId, long productId) {
        Product product = findById(productId);
        product.checkIsDetailPageMine(userId);
        if (product.checkIsMine(userId)) {
            productRepository.delete(product);
        }
    }

    @Transactional
    public void changeLike(long productId, long userId) {
        Member member = memberService.findMemberById(userId);
        Product product = findById(productId);
        Optional<Interested> interested = interestedRepository.findByMemberAndProduct(member, product);
        if (interested.isPresent()) {
            log.debug("이미 좋아요 누른경우 ======================");
            Interested existInterested = interested.get();
            product.decreaseCountView();
            interestedRepository.delete(existInterested);
            existInterested.deleteInterested(existInterested, member, product);
            return;
        }
        log.debug("처음 좋아요  누른경우 ======================");
        Interested newInterested = new Interested();
        newInterested.changeInterested(newInterested, member, product);
        interestedRepository.save(newInterested);
        product.increaseCountView();
        log.debug("상품 좋아요 수 = {}", product.getCountLike());
    }


    @Transactional
    public void changeStatus(long productId, long userId, Integer statusRequest) {
        Product product = findById(productId);
        Long memberId = product.getMember().getId();
        if (memberId == userId) {
            product.updateStatus(statusRequest);
            return;
        }
        throw new NotUserMineProductException();
    }


    private void checkIsMine(long userId, long product) {
        if (product == userId) {
            return;
        }
        throw new NotUserMineProductException();
    }

    public Product findById(long productId) {
        return productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
    }
}
