package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品管理")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //新增菜品后需要清理redis中对应分类的菜品缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
//        log.info("清理redis中菜品缓存，key：{}", key);
        return Result.success();
    }



    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /*
        * 批量删除菜品
        * 参数：ids
        * 返回值：Result
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public  Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品：{}", ids);
        dishService.deleteBatch(ids);

        //删除菜品后需要清理redis中对应分类的菜品缓存数据
        //由于不知道删除的菜品属于哪个分类，所以选择清理所有分类的菜品缓存
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);

    }
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        //更新dish表基本信息
        dishService.updateWithFlavor(dishDTO);

        //修改菜品后需要清理redis中对应分类的菜品缓存数据
        cleanCache("dish_*");
        return Result.success();
    }


    @PostMapping("/status/{status}")
    @ApiOperation("起售/停售菜品")
    public  Result<String> startOrStop(@PathVariable Integer status, Long id){
        dishService.startOrStop(status, id);

        //起售/停售菜品后需要清理redis中对应分类的菜品缓存数据
        cleanCache("dish_*");

        return Result.success();
    }


    /*
        * 根据分类id查询菜品列表
        * 参数：categoryId
        * 返回值：return
     */
    @GetMapping("/list")
    @ApiOperation("根据条件查询菜品列表")
    public Result<List<Dish>> list(Long categoryIDd){
        List<Dish> list = dishService.list(categoryIDd);
        return Result.success(list);
    }

    //清理缓存方法
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
