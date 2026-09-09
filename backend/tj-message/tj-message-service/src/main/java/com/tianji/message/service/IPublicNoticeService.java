package com.tianji.message.service;

import com.tianji.message.domain.po.NoticeTemplate;
import com.tianji.message.domain.po.PublicNotice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 公告消息模板 服务类
 * </p>
 *
 * @since 2022-08-19
 */
public interface IPublicNoticeService extends IService<PublicNotice> {

    void saveNoticeOfTemplate(NoticeTemplate noticeTemplate);
}
