package com.weimob.saas.ec.limitation.export;

import com.weimob.saas.ec.common.export.BaseExportService;
import com.weimob.saas.ec.limitation.service.LimitationQueryService;
import org.springframework.stereotype.Service;

/**
 * @author lujialin
 * @description 限购查询export层
 * @date 2018/5/29 10:41
 */
@Service(value = "limitationQueryExportService")
public class LimitationQueryExportService extends BaseExportService implements LimitationQueryService {

}
