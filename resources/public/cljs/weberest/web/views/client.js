goog.provide('weberest.web.views.client');
goog.require('cljs.core');
goog.require('domina.css');
goog.require('domina');
goog.require('dommy.template');
goog.require('enfocus.core');
/**
* @param {...*} var_args
*/
weberest.web.views.client.create_svg_element = (function() { 
var create_svg_element__delegate = function (tag,p__9043){
var map__9045 = p__9043;
var map__9045__$1 = ((cljs.core.seq_QMARK_.call(null,map__9045))?cljs.core.apply.call(null,cljs.core.hash_map,map__9045):map__9045);
var styles = cljs.core._lookup.call(null,map__9045__$1,"\uFDD0'styles",cljs.core.ObjMap.EMPTY);
var attrs = cljs.core._lookup.call(null,map__9045__$1,"\uFDD0'attrs",cljs.core.ObjMap.EMPTY);
var text = cljs.core._lookup.call(null,map__9045__$1,"\uFDD0'text","");
return domina.set_text_BANG_.call(null,domina.set_styles_BANG_.call(null,domina.set_attrs_BANG_.call(null,document.createElementNS("http://www.w3.org/2000/svg",tag),attrs),styles),text);
};
var create_svg_element = function (tag,var_args){
var p__9043 = null;
if (goog.isDef(var_args)) {
  p__9043 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);
} 
return create_svg_element__delegate.call(this, tag, p__9043);
};
create_svg_element.cljs$lang$maxFixedArity = 1;
create_svg_element.cljs$lang$applyTo = (function (arglist__9046){
var tag = cljs.core.first(arglist__9046);
var p__9043 = cljs.core.rest(arglist__9046);
return create_svg_element__delegate(tag, p__9043);
});
create_svg_element.cljs$lang$arity$variadic = create_svg_element__delegate;
return create_svg_element;
})()
;
weberest.web.views.client.value_dot_increase_factor = 3;
weberest.web.views.client.rgb = (function rgb(r,g,b){
return [cljs.core.str("rgb("),cljs.core.str(r),cljs.core.str(","),cljs.core.str(g),cljs.core.str(","),cljs.core.str(b),cljs.core.str(")")].join('');
});
weberest.web.views.client.dot_label = (function dot_label(id,x,y,text,options){
return cljs.core.PersistentVector.fromArray(["\uFDD0'text",cljs.core.merge.call(null,cljs.core.ObjMap.fromObject(["\uFDD0'id","\uFDD0'x","\uFDD0'y","\uFDD0'label-font-family","\uFDD0'label-font-size","\uFDD0'visibility","\uFDD0'fill"],{"\uFDD0'id":id,"\uFDD0'x":x,"\uFDD0'y":y,"\uFDD0'label-font-family":"Verdana","\uFDD0'label-font-size":"55px","\uFDD0'visibility":"\uFDD0'visible","\uFDD0'fill":weberest.web.views.client.rgb.call(null,100,100,150)}),options),text], true);
});
/**
* @param {...*} var_args
*/
weberest.web.views.client.create_dot_label = (function() { 
var create_dot_label__delegate = function (p__9047){
var map__9049 = p__9047;
var map__9049__$1 = ((cljs.core.seq_QMARK_.call(null,map__9049))?cljs.core.apply.call(null,cljs.core.hash_map,map__9049):map__9049);
var color = cljs.core._lookup.call(null,map__9049__$1,"\uFDD0'color","black");
var text = cljs.core._lookup.call(null,map__9049__$1,"\uFDD0'text","");
var y = cljs.core._lookup.call(null,map__9049__$1,"\uFDD0'y",0);
var x = cljs.core._lookup.call(null,map__9049__$1,"\uFDD0'x",0);
return weberest.web.views.client.create_svg_element.call(null,"text","\uFDD0'text",text,"\uFDD0'attrs",cljs.core.ObjMap.fromObject(["\uFDD0'x","\uFDD0'y","\uFDD0'transform"],{"\uFDD0'x":x,"\uFDD0'y":y,"\uFDD0'transform":"translate(-8, -20)"}),"\uFDD0'styles",cljs.core.ObjMap.fromObject(["\uFDD0'fill"],{"\uFDD0'fill":color}));
};
var create_dot_label = function (var_args){
var p__9047 = null;
if (goog.isDef(var_args)) {
  p__9047 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return create_dot_label__delegate.call(this, p__9047);
};
create_dot_label.cljs$lang$maxFixedArity = 0;
create_dot_label.cljs$lang$applyTo = (function (arglist__9050){
var p__9047 = cljs.core.seq(arglist__9050);;
return create_dot_label__delegate(p__9047);
});
create_dot_label.cljs$lang$arity$variadic = create_dot_label__delegate;
return create_dot_label;
})()
;
weberest.web.views.client.enlarge_value_dot = (function enlarge_value_dot(evt){
var ct = evt.currentTarget;
var map__9053 = domina.attrs.call(null,ct);
var map__9053__$1 = ((cljs.core.seq_QMARK_.call(null,map__9053))?cljs.core.apply.call(null,cljs.core.hash_map,map__9053):map__9053);
var old_r = cljs.core._lookup.call(null,map__9053__$1,"\uFDD0'r",null);
var x = cljs.core._lookup.call(null,map__9053__$1,"\uFDD0'cx",null);
var y = cljs.core._lookup.call(null,map__9053__$1,"\uFDD0'cy",null);
var color = cljs.core._lookup.call(null,map__9053__$1,"\uFDD0'fill",null);
var day = cljs.core._lookup.call(null,map__9053__$1,"\uFDD0'data-x",null);
var value = cljs.core._lookup.call(null,map__9053__$1,"\uFDD0'data-y",null);
var map__9054 = domina.attrs.call(null,ct.parentNode);
var map__9054__$1 = ((cljs.core.seq_QMARK_.call(null,map__9054))?cljs.core.apply.call(null,cljs.core.hash_map,map__9054):map__9054);
var label = cljs.core._lookup.call(null,map__9054__$1,"\uFDD0'data-label",null);
var unit = cljs.core._lookup.call(null,map__9054__$1,"\uFDD0'data-unit",null);
var dl = weberest.web.views.client.create_dot_label.call(null,"\uFDD0'x",x,"\uFDD0'y",y,"\uFDD0'color",color,"\uFDD0'text",[cljs.core.str("Tag: "),cljs.core.str(day),cljs.core.str(" | "),cljs.core.str(label),cljs.core.str(": "),cljs.core.str(value),cljs.core.str(unit)].join(''));
domina.insert_after_BANG_.call(null,ct,dl);
return domina.set_attr_BANG_.call(null,ct,"\uFDD0'r",(old_r * weberest.web.views.client.value_dot_increase_factor));
});
weberest.web.views.client.shrink_value_dot = (function shrink_value_dot(evt){
var ct = evt.currentTarget;
var dl = ct.nextElementSibling;
if(cljs.core.truth_((function (){var and__3822__auto__ = dl;
if(cljs.core.truth_(and__3822__auto__))
{return cljs.core._EQ_.call(null,"text".localName);
} else
{return and__3822__auto__;
}
})()))
{domina.destroy_BANG_.call(null,dl);
} else
{}
return domina.set_attr_BANG_.call(null,ct,"\uFDD0'r",(domina.attr.call(null,ct,"\uFDD0'r") / weberest.web.views.client.value_dot_increase_factor));
});
weberest.web.views.client.setup_value_display_svg_listeners = (function setup_value_display_svg_listeners(){
return enfocus.core.at.call(null,document,cljs.core.PersistentVector.fromArray(["circle"], true),enfocus.core.en_do__GT_.call(null,enfocus.core.en_listen.call(null,"\uFDD0'mouseenter",weberest.web.views.client.enlarge_value_dot),enfocus.core.en_listen.call(null,"\uFDD0'mouseleave",weberest.web.views.client.shrink_value_dot)));
});
goog.exportSymbol('weberest.web.views.client.setup_value_display_svg_listeners', weberest.web.views.client.setup_value_display_svg_listeners);
