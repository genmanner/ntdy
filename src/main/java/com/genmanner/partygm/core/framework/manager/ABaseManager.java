package com.genmanner.partygm.core.framework.manager;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.genmanner.partygm.core.common.framework.Page;
import com.genmanner.partygm.core.common.framework.UUIDUtils;
import com.genmanner.partygm.core.framework.domain.ABean;
import com.genmanner.partygm.core.framework.exception.ManagerException;
import com.genmanner.partygm.core.framework.utils.BeanUtils;
import com.genmanner.partygm.core.framework.utils.SqlId;
/**
 * 实现manager层基本方法
 *  
 */
public abstract class ABaseManager<T extends ABean>{
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired(required = true)
	protected SqlSession sqlSessionTemplate;
	
	
	@Autowired
	protected JdbcTemplate jdbcTemplate_mysql;
	
	@Autowired(required = true)
	protected SqlSession sqlSessionTemplate_mysql;
	
	/**
	 * 空间属性分割符
	 */
	public static final String SQLNAME_SEPARATOR = ".";

	/**
	 * SqlMapping命名空间 
	 */
	private String sqlNamespace = getDefaultSqlNamespace();

	/**
	 * 获取泛型类型的实体对象类全名
	 */
	protected String getDefaultSqlNamespace() {
		Class<?> genericClass = BeanUtils.getGenericClass(this.getClass());
		return genericClass == null ? null : genericClass.getName();
	}

	/**
	 * 获取SqlMapping命名空间 
	 * @return SqlMapping命名空间 
	 */
	public String getSqlNamespace() {
		return sqlNamespace;
	}

	/**
	 * 设置SqlMapping命名空间。 以改变默认的SqlMapping命名空间，
	 * <br>不能滥用此方法随意改变SqlMapping命名空间。 
	 * @param sqlNamespace SqlMapping命名空间 
	 */
	public void setSqlNamespace(String sqlNamespace) {
		this.sqlNamespace = sqlNamespace;
	}

	/**
	 * 将SqlMapping命名空间与给定的SqlMapping名组合在一起。
	 * @param sqlName SqlMapping名 
	 * @return 组合了SqlMapping命名空间后的完整SqlMapping名 
	 */
	protected String getSqlName(String sqlName) {
		return sqlNamespace + SQLNAME_SEPARATOR + sqlName;
	}

	/**
	 * 生成主键值。 默认使用方法
	 * 如果需要生成主键，需要由子类重写此方法根据需要的方式生成主键值。 
	 * @param entity 要持久化的对象 
	 */
	protected String generateId() {
		return UUIDUtils.create();
	}

	/**
	 * 按主键查询一条记录
	 * @param id
	 * @return 当前实体对象
	 */
	public T selectById(String id) {
		Assert.notNull(id);
		try {
			return sqlSessionTemplate.selectOne(getSqlName(SqlId.SQL_SELECT_BY_ID), id);
		} catch (Exception e) {
			throw new ManagerException(String.format("根据ID查询对象出错！语句：%s", getSqlName(SqlId.SQL_SELECT_BY_ID)), e);
		}
	}

	/**
	 * 按条件查询全部记录
	 * @param query 查询条件
	 * @return 当前实体对象集合
	 */
	public List<T> selectList(Page page) {
		try {
			return sqlSessionTemplate.selectList(getSqlName(SqlId.SQL_SELECT), page);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ManagerException(String.format("查询对象列表出错！语句：%s", getSqlName(SqlId.SQL_SELECT)), e);
		}
	}

	/**
	 * 查询全部记录
	 * @return 当前实体表中的全部集合
	 */
	public List<T> selectAll() {
		try {
			return sqlSessionTemplate.selectList(getSqlName(SqlId.SQL_SELECT));
		} catch (Exception e) {
			throw new ManagerException(String.format("查询所有对象列表出错！语句：%s", getSqlName(SqlId.SQL_SELECT)), e);
		}
	}
	
	/**
	 * 分页按条查询查询记录
	 * @param query 查询条件
	 * @param page 分页参数
	 * @return 当前实体对象集合分页
	 */
	public Page selectPageList(Page page) {
		try {
			page.setPageState(true);
			List contentList = sqlSessionTemplate.selectList(getSqlName(SqlId.SQL_SELECT),page);
			page.setRes(contentList);
			return page;
		} catch (Exception e) {
			throw new ManagerException(String.format("根据分页对象查询列表出错！语句:%s", getSqlName(SqlId.SQL_SELECT)), e);
		}
	}
	
	/**
	 * 分页无条件查询记录
	 * @param page 分页参数
	 * @return 当前实体对象全部集合中分页
	 */
	public Page selectPageAll(Page page) {
		try {
			page.setPageState(true);
			List contentList = sqlSessionTemplate.selectList(getSqlName(SqlId.SQL_SELECT), page);
			page.setRes(contentList);
			return page;
		} catch (Exception e) {
			throw new ManagerException(String.format("根据分页对象查询列表出错！语句:%s", getSqlName(SqlId.SQL_SELECT)), e);
		}
	}
	
	/**
	 * 查询总记录数
	 * @return 当前实体总记录数
	 */
	public Long selectCount() {
		try {
			return sqlSessionTemplate.selectOne(getSqlName(SqlId.SQL_SELECT_COUNT));
		} catch (Exception e) {
			throw new ManagerException(String.format("查询对象总数出错！语句：%s", getSqlName(SqlId.SQL_SELECT_COUNT)), e);
		}
	}

	
	/**
	 * 按条件查询总记录数
	 * @param query 查询 条件
	 * @return 当前实体总记录数按条件
	 */
	public Long selectCount(Page page) {
		try {
			return sqlSessionTemplate.selectOne(getSqlName(SqlId.SQL_SELECT_COUNT), page);
		} catch (Exception e) {
			throw new ManagerException(String.format("查询对象总数出错！语句：%s", getSqlName(SqlId.SQL_SELECT_COUNT)), e);
		}
	}

	/**
	 * 插入一条记录
	 * @param entity 插入的BEAN
	 */
	public int insert(T entity) {
		Assert.notNull(entity);
		try {
			if (StringUtils.isBlank(entity.getId()))
				entity.setId(generateId());
			return sqlSessionTemplate.insert(getSqlName(SqlId.SQL_INSERT), entity);
		} catch (Exception e) {
			throw new ManagerException(String.format("添加对象出错！语句：%s", getSqlName(SqlId.SQL_INSERT)), e);
		}
	}
	
	/**
	 * 按条件删除记录
	 * @param query 条件
	 * @return 删除 记录条数
	 */
	public int delete(Page page) {
		
		try {
			Assert.notNull(page);
			return sqlSessionTemplate.delete(getSqlName(SqlId.SQL_DELETE), page);
		} catch (Exception e) {
			throw new ManagerException(String.format("删除对象出错！语句：%s", getSqlName(SqlId.SQL_DELETE)), e);
		}
	}
	
	/**
	 * 用ID来删除一条记录
	 * @param id  ID
	 * @return 删除 记录条数
	 */
	public int deleteById(String id) {
		Assert.notNull(id);
		try {
			return sqlSessionTemplate.delete(getSqlName(SqlId.SQL_DELETE_BY_ID), id);
		} catch (Exception e) {
			throw new ManagerException(String.format("根据ID删除对象出错！语句：%s", getSqlName(SqlId.SQL_DELETE_BY_ID)), e);
		}
	}
	/**
	 * 删除表中所有记录
	 * @return 返回记录条数
	 */
	public int deleteAll() {
		try {
			return sqlSessionTemplate.delete(getSqlName(SqlId.SQL_DELETE));
		} catch (Exception e) {
			throw new ManagerException(String.format("删除所有对象出错！语句：%s", getSqlName(SqlId.SQL_DELETE)), e);
		}
	}
	
	/**
	 * 对一条记录更新，利用ID 全部字段更新
	 * @param entity 需要更新的记录实体
	 * @return 更新记录条数
	 */
	public int updateById(T entity) {
		Assert.notNull(entity);
		try {
			return sqlSessionTemplate.update(getSqlName(SqlId.SQL_UPDATE_BY_ID), entity);
		} catch (Exception e) {
			throw new ManagerException(String.format("根据ID更新对象出错！语句：%s", getSqlName(SqlId.SQL_UPDATE_BY_ID)), e);
		}
	}
	
	/**
	 * 对一条记录更新，利用ID 只更新实体中不为NULL的字段
	 * @param entity 需要更新实体
	 * @return 更新记录条数
	 */
	public int updateByIdSelective(T entity) {
		Assert.notNull(entity);
		try {
			return sqlSessionTemplate.update(getSqlName(SqlId.SQL_UPDATE_BY_ID_SELECTIVE), entity);
		} catch (Exception e) {
			throw new ManagerException(String.format("根据ID更新对象某些属性出错！语句：%s", getSqlName(SqlId.SQL_UPDATE_BY_ID_SELECTIVE)),
					e);
		}
	}
	
	/**
	 * 批量删除对ID
	 * @param idList id集合
	 */
	public void deleteByIdInBatch(List<String> idList) {
		if (idList == null || idList.isEmpty())
			return;
		for (String id : idList) {
			this.deleteById(id);
		}
	}
	
	/**
	 * 批量更新（对不为NULL字段更新）
	 * @param entityList 数据集合
	 */
	public void updateInBatch(List<T> entityList) {
		if (entityList == null || entityList.isEmpty())
			return;
		for (T entity : entityList) {
			this.updateByIdSelective(entity);
		}
	}
	
	/**
	 * 批量添加
	 * @param entityList 数据集合
	 */
	public void insertInBatch(List<T> entityList) {
		if (entityList == null || entityList.isEmpty())
			return;
		for (T entity : entityList) {
			this.insert(entity);
		}
	}
}
