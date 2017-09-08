package com.xuanwu.flowengine.cmd;

import com.xuanwu.flowengine.entity.Page;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;

import java.util.List;

/**
 * 自定义抽象查询类
 * <p>
 * Created by jkun on 2017/3/16.
 *
 * @author jkun
 */
public abstract class AbstractQuery<E> implements Command<Object> {

    protected int pageIndex = 0;
    protected int pageSize = Integer.MAX_VALUE;
    protected CommandExecutor commandExecutor;
    protected ResultType resultType;

    public AbstractQuery(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    private static enum ResultType {
        LIST, LIST_PAGE, SINGLE_RESULT, COUNT
    }

    public List<E> list() {
        this.resultType = ResultType.LIST;
        if (null != commandExecutor) {
            return (List<E>) commandExecutor.execute(this);
        }

        return executeList(Context.getCommandContext());
    }

    public Page<E> listPage(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.resultType = ResultType.LIST_PAGE;
        if (null != commandExecutor) {
            return (Page<E>) commandExecutor.execute(this);
        }

        return executePage(Context.getCommandContext());
    }

    public E singleResult() {
        this.resultType = ResultType.SINGLE_RESULT;
        if (null != commandExecutor) {
            return (E) commandExecutor.execute(this);
        }

        return executeSingleResult(Context.getCommandContext());
    }

    public abstract List<E> executeList(CommandContext commandContext);

    public abstract Page<E> executePage(CommandContext commandContext);

    public E executeSingleResult(CommandContext commandContext) {
        List<E> result = executeList(commandContext);
        if (result.size() == 1) {
            return result.get(0);
        } else if (result.size() > 1) {
            throw new ActivitiException("Query return " + result.size() + " results instead of max 1");
        }

        return null;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        if (resultType == ResultType.SINGLE_RESULT) {
            return executeSingleResult(commandContext);
        } else if (resultType == ResultType.LIST) {
            return executeList(commandContext);
        } else if (resultType == ResultType.LIST_PAGE) {
            return executePage(commandContext);
        }

        return null;
    }
}
