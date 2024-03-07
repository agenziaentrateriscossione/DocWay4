package it.tredi.dw4.renderer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.CommandLinkRenderer;

/**
 * Override del renderer per CommandLink in modo da evitare l'apertura in multischeda di docway tramite menù contestuale dai link dell'applicativo
 * @author mbernardini
 */
public class Dw4CommandLinkRenderer extends CommandLinkRenderer {
	
	private static final Attribute[] ATTRIBUTES = AttributeManager.getAttributes(AttributeManager.Key.COMMANDLINK);

	@Override
    protected void renderAsActive(FacesContext context, UIComponent command) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        assert(writer != null);
        String formClientId = RenderKitUtils.getFormClientId(command, context);
        if (formClientId == null) {
            return;
        }

        //make link act as if it's a button using javascript        
        writer.startElement("a", command);
        writeIdAttributeIfNecessary(context, writer, command);
        
        // mbernardini 17/01/2018 : modificati gli href in data-href per prevenire l'apertura dei link in multischeda (attivita' non supportata su DocWay)
        // il browser non mostra piu' il menu' contestuale che permette di aprire il link su una nuova scheda o finestra
        writer.writeAttribute("data-href", "#", "data-href");
        
        RenderKitUtils.renderPassThruAttributes(context,
                                                writer,
                                                command,
                                                ATTRIBUTES,
                                                getNonOnClickBehaviors(command));

        RenderKitUtils.renderXHTMLStyleBooleanAttributes(writer, command);

        String target = (String) command.getAttributes().get("target");
        if (target != null) {
            target = target.trim();
        } else {
            target = "";
        }

        Collection<ClientBehaviorContext.Parameter> params = getBehaviorParameters(command);
        RenderKitUtils.renderOnclick(context, 
                                     command,
                                     params,
                                     target,
                                     true);

        writeCommonLinkAttributes(writer, command);

        // render the current value as link text.
        writeValue(command, writer);
        writer.flush();
    }
	
	// Returns the Behaviors map, but only if it contains some entry other
    // than those handled by renderOnclick().  This helps us optimize
    // renderPassThruAttributes() in the very common case where the
    // link only contains an "action" (or "click") Behavior.  In that
    // we pass a null Behaviors map into renderPassThruAttributes(),
    // which allows us to take a more optimized code path.
    private static Map<String, List<ClientBehavior>> getNonOnClickBehaviors(UIComponent component) {

        return getPassThruBehaviors(component, "click", "action");
    }
	
}
