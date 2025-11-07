package com.saury.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.dto.MessageDTO;
import com.saury.blog.vo.MessageVO;

/**
 * 留言服务接口
 *
 * @author Saury
 */
public interface MessageService {

    /**
     * 分页查询留言列表
     *
     * @param page   页码
     * @param size   每页数量
     * @param status 状态（前台只查询已通过的）
     * @return 留言分页列表
     */
    Page<MessageVO> getMessagePage(Integer page, Integer size, Integer status);

    /**
     * 保存留言
     *
     * @param messageDTO 留言DTO
     * @param userId     用户ID（可为空）
     * @param ip         IP地址
     * @return 留言ID
     */
    Long saveMessage(MessageDTO messageDTO, Long userId, String ip);

    /**
     * 删除留言
     *
     * @param id 留言ID
     */
    void deleteMessage(Long id);

    /**
     * 审核留言
     *
     * @param id     留言ID
     * @param status 状态
     */
    void auditMessage(Long id, Integer status);
}


