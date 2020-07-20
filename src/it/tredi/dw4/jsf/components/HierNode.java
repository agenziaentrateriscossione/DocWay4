package it.tredi.dw4.jsf.components;

import java.io.IOException;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.dom4j.Element;

@FacesComponent("it.tredi.dw4.jsf.components.HierNode")
public class HierNode extends UIComponentBase {
	private static String class_icon_b 		= "hiernode_icon_b";
	private static String class_icon_bb 	= "hiernode_icon_bb";
	private static String class_icon_c 		= "hiernode_icon_c";
	private static String class_icon_cb		= "hiernode_icon_cb";
	private static String class_icon_cbo	= "hiernode_icon_cbo";
	private static String class_icon_cbc	= "hiernode_icon_cbc";
	private static String class_icon_cc		= "hiernode_icon_cc";
	private static String class_icon_co		= "hiernode_icon_co";
	private static String class_icon_fc		= "hiernode_icon_fc";
	private static String class_icon_fo		= "hiernode_icon_fo";
	private static String class_icon_l		= "hiernode_icon_l";
	private static String class_icon_ra		= "hiernode_icon_ra";
	private static String class_icon_ro		= "hiernode_icon_ro";
	private static String class_icon_rc		= "hiernode_icon_rc";
	
	private static String class_foldertree	= "hiernode_foldertree";
	private static String class_title		= "hiernode_title";
	private static String class_title_paragraph	= "hiernode_title_paragraph";
	private static String class_toggle_link = "hiernode_toggle_link";
	
	private HtmlCommandLink toggleLink;
	private HtmlCommandLink titleLink;
	
	public HierNode() {
		this.toggleLink = null;
		this.titleLink = null;
	}
	
	@Override
	public String getFamily() {
		return "it.tredi.dw4.jsf.components";
	}
	
	@Override
	public void encodeEnd(FacesContext ctx) throws IOException {
		Map<String, Object> attributes = getAttributes();
		Element hierElement = (Element) attributes.get("value");
		Integer level = (attributes.get("level") != null)? Integer.parseInt((String) attributes.get("level")) : 2;
		String showAction = (String) attributes.get("showAction");
		
		//se necessario crea i componenti figli
		if (this.toggleLink == null) {
			this.toggleLink = (HtmlCommandLink) ctx.getApplication().createComponent(HtmlCommandLink.COMPONENT_TYPE);
			/*
			toggleLink.setParent(this);
			this.getChildren().add(toggleLink);
			*/
		}
		
		if (this.titleLink == null) {
			this.titleLink = (HtmlCommandLink) ctx.getApplication().createComponent(HtmlCommandLink.COMPONENT_TYPE);
			/*
			titleLink.setParent(this);
			this.getChildren().add(titleLink);
			*/
			
			if (showAction != null && showAction.length() > 0) {
				ExpressionFactory elFactory = ctx.getApplication().getExpressionFactory();
				MethodExpression showActionMethod = elFactory.createMethodExpression(ctx.getELContext(), "#{" + showAction + "}", String.class, new Class[] {});
				titleLink.setActionExpression(showActionMethod);
			}
		}
		
		ResponseWriter out = ctx.getResponseWriter();
		
		out.startElement("div", null);
		computeStructure(ctx, hierElement, level, out);
		out.endElement("div");
	}
	
	private void computeStructure(FacesContext ctx, Element hierElement, int startLevel, ResponseWriter out) throws IOException {
		
		String brothers = hierElement.attributeValue("brothers", "");
		String sons = hierElement.attributeValue("sons", "");
		String opened = hierElement.attributeValue("opened", "");
		String depth = hierElement.attributeValue("depth", "");
		String title = hierElement.attributeValue("title", "");
		String ndoc = hierElement.attributeValue("ndoc", "");
		String titlePath = hierElement.attributeValue("titlePath", "");
		
		for (int level = startLevel; level > 1 && brothers.length() >= level; level++) {
			
			if (brothers.length() == level) { //ultimo ramo
				//level - 1 poiché è una traduzione da xslt dove le stringhe partono da indice 1
				if (brothers.substring(level - 1, 1).equals("1")) { //seguono fratelli
					if (sons.equals("1")) { //ci sono figli
						if (opened.equals("1")) { //corner with brothers and sons opened
							out.startElement("span", null);
							out.writeAttribute("class", HierNode.class_icon_cbo, null);

							out.startElement("span", null);
							out.writeAttribute("class", HierNode.class_foldertree, null);
							
							toggleLink.setId("hier_tile_lnk_" + ndoc);
							toggleLink.setValue("-");
							toggleLink.setStyleClass(HierNode.class_toggle_link);
							toggleLink.encodeAll(ctx);
							
							out.endElement("span");
							out.endElement("span");
						}
						else { //corner with brothers and sons closed
							out.startElement("span", null);
							out.writeAttribute("class", HierNode.class_icon_cbc, null);

							out.startElement("span", null);
							out.writeAttribute("class", HierNode.class_foldertree, null);
							
							toggleLink.setId("hier_tile_lnk_" + ndoc);
							toggleLink.setValue("+");
							toggleLink.setStyleClass(HierNode.class_toggle_link);
							
							toggleLink.encodeAll(ctx);
							
							out.endElement("span");
							out.endElement("span");
						}
					}
					else { //leaf corner with brothers
						out.startElement("span", null);
						out.writeAttribute("class", HierNode.class_icon_cb, null);
						out.writeText("&#160;", null);
						out.endElement("span");
					}
				}
				else { //no fratelli a seguire
					if (sons.equals("1")) { //ci sono figli
						if (opened.equals("1")) { //corner without brothers, with sons opened
							out.startElement("span", null);
							out.writeAttribute("class", HierNode.class_icon_co, null);
							
							out.startElement("span", null);
							out.writeAttribute("class", HierNode.class_foldertree, null);
							
							toggleLink.setId("hier_tile_lnk_" + ndoc);
							toggleLink.setValue("-");
							toggleLink.setStyleClass(HierNode.class_toggle_link);
							
							toggleLink.encodeAll(ctx);
							
							out.endElement("span");
							out.endElement("span");
						}
						else { //corner without brothers, with sons closed
							out.startElement("span", null);
							out.writeAttribute("class", HierNode.class_icon_cc, null);
							
							out.startElement("span", null);
							out.writeAttribute("class", HierNode.class_foldertree, null);
							
							toggleLink.setId("hier_tile_lnk_" + ndoc);
							toggleLink.setValue("+");
							toggleLink.setStyleClass(HierNode.class_toggle_link);
							
							toggleLink.encodeAll(ctx);
							
							out.endElement("span");
							out.endElement("span");
						}
					}
					else { //leaf corner without brothers
						out.startElement("span", null);
						out.writeAttribute("class", HierNode.class_icon_c, null);
						out.writeText("&#160;", null);
						out.endElement("span");
					}
				}
			}
			else {
				//level - 1 poiché è una traduzione da xslt dove le stringhe partono da indice 1
				if (brothers.substring(level - 1, 1).equals("1")) { //seguono fratelli
					out.startElement("span", null);
					out.writeAttribute("class", HierNode.class_icon_bb, null);
					out.writeText("&#160;", null);
					out.endElement("span");
				}
				else { //nessun fratello
					out.startElement("span", null);
					out.writeAttribute("class", HierNode.class_icon_b, null);
					out.writeText("&#160;", null);
					out.endElement("span");
				}
			}
		}
		
		if (depth.equals("1")) { //radice
			if (sons.equals("1")) { //ci sono figli
				if (opened.equals("1")) { //radice aperta
					out.startElement("span", null);
					out.writeAttribute("class", HierNode.class_icon_ro, null);
					out.startElement("a", null);
					out.writeAttribute("class", HierNode.class_foldertree, null);
					out.writeAttribute("href", "#", null);
					out.writeText("ro ", null);
					out.endElement("a");
					out.endElement("span");
				}
				else { //radice chiusa
					out.startElement("span", null);
					out.writeAttribute("class", HierNode.class_icon_rc, null);
					out.startElement("a", null);
					out.writeAttribute("class", HierNode.class_foldertree, null);
					out.writeAttribute("href", "#", null);
					out.writeText("rc ", null);
					out.endElement("a");
					out.endElement("span");
				}
			}
			else { //solo radice
				out.startElement("span", null);
				out.writeAttribute("class", HierNode.class_icon_ra, null);
				out.writeText("ra ", null);
				out.endElement("span");
			}
		}
		else { //figlio
			if (sons.equals("1")) { //ci sono figli
				if (opened.equals("1")) { //cartella aperta
					out.startElement("span", null);
					out.writeAttribute("class", HierNode.class_icon_fo, null);
					out.startElement("a", null);
					out.writeAttribute("class", HierNode.class_foldertree, null);
					out.writeAttribute("href", "#", null);
					out.writeText("fo ", null);
					out.endElement("a");
					out.endElement("span");
				}
				else { //cartella chiusa
					out.startElement("span", null);
					out.writeAttribute("class", HierNode.class_icon_fc, null);
					out.startElement("a", null);
					out.writeAttribute("class", HierNode.class_foldertree, null);
					out.writeAttribute("href", "#", null);
					out.writeText("fc ", null);
					out.endElement("a");
					out.endElement("span");
				}
			}
			else { //foglia
				out.startElement("span", null);
				out.writeAttribute("class", HierNode.class_icon_l, null);
				out.writeText("l ", null);
				out.endElement("span");
			}
		}
		
		out.startElement("span", null);
		out.writeAttribute("class", HierNode.class_title, null);
		
		if (titlePath.length() > 0)
			out.writeAttribute("title", titlePath, null);

		out.startElement("span", null);
		out.writeAttribute("class", HierNode.class_foldertree, null);
		
		titleLink.setId("hier_tile_lnk_" + ndoc);
		titleLink.setValue(title);
		titleLink.setStyleClass(HierNode.class_title_paragraph);
		titleLink.encodeAll(ctx);
		
		out.endElement("span");
		out.endElement("span");
		
		out.startElement("input", null);
		out.writeAttribute("type", "hidden", null);
		out.writeAttribute("id", "hier_cmd_input:" + ndoc, null);
		out.writeAttribute("name", "hier_cmd_input:" + ndoc, null);
		out.writeAttribute("value", "puppa", null);
		out.endElement("input");
	}
	
	@Override
	public void decode(FacesContext context) {
		if (context == null) {
            throw new NullPointerException();
        }
		
		Map<String, Object> attributes = getAttributes();
		Element hierElement = (Element) attributes.get("value");
		String ndoc = hierElement.attributeValue("ndoc", "");
		
		Map<String, String> parameterMap = context.getExternalContext().getRequestParameterMap();
		String cmdSelected = parameterMap.get("hier_cmd_input:" + ndoc);
		if (cmdSelected != null && cmdSelected.length() > 0) {
			Object bindingObj = attributes.get("binding");
			if (bindingObj != null && bindingObj instanceof HierNodeBinding) {
				HierNodeBinding binding = (HierNodeBinding) bindingObj;
				binding.setSelectedNDoc(ndoc);
			}
			
				
		}
	}
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
