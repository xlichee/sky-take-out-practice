package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    // 新增菜品
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id = #{dishId}")
    Dish getById(Long id);


    /*
        * 根据id批量删除菜品
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /*
    根据菜品集合批量删除菜品
     */
    void deleteByIds(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void updateById(Dish dish);

    List<Dish> list(Dish dish);

    @Select("select * from dish where id in (select dish_id from setmeal_dish where setmeal_id = #{setmealId})")
    List<Dish> getBySetmealId(Long setmealId);
}
