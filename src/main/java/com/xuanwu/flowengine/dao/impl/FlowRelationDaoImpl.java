package com.xuanwu.flowengine.dao.impl;

import com.xuanwu.flowengine.dao.FlowRelationDao;
import com.xuanwu.flowengine.entity.FlowCategoryRelationEntity;
import com.xuanwu.flowengine.entity.Page;
import com.xuanwu.flowengine.entity.dto.FlowCategoryRelationListDto;
import com.xuanwu.flowengine.util.ParamMap;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * 流程关联关系Dao实现类
 * Created by jkun on 2017/3/17.
 *
 * @author jkun
 */
public class FlowRelationDaoImpl extends AbstractManager implements FlowRelationDao {

    @Override
    public void insertFlowRelation(FlowCategoryRelationEntity entity) {
        getDbSqlSession().getSqlSession().insert(this.getClass().getName() + ".insert", entity);
    }

    @Override
    public void updateFlowRelation(FlowCategoryRelationEntity entity) {
        getDbSqlSession().getSqlSession().update(this.getClass().getName() + ".update", entity);
    }

    @Override
    public void deleteFlowRelation(Long flowDefineCode) {
        ParamMap map = ParamMap.getInstance().add("flowRelationCode", flowDefineCode);
        getDbSqlSession().getSqlSession().update(this.getClass().getName() + ".delete", map);
    }

    @Override
    public Page<FlowCategoryRelationListDto> getFlowListPage(Long directoryCode, String flowModelName, int pageIndex, int pageSize) {
        int offset = (pageIndex - 1) * pageSize;
        ParamMap map = ParamMap.getInstance().add("directoryCode", directoryCode).add("modelName", flowModelName)
                .add("limit", pageSize).add("offset", offset);
        SqlSession session = getDbSqlSession().getSqlSession();
        long count = session.selectOne(this.getClass().getName() + ".selectcount", map);
        if (count == 0) {
            return new Page<>(pageSize, count, null);
        }

        List<FlowCategoryRelationListDto> list = session.selectList(this.getClass().getName() + ".selectlist", map);
        Page<FlowCategoryRelationListDto> page = new Page<FlowCategoryRelationListDto>(pageSize, count, list);
        return page;
    }
}
