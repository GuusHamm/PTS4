package nl.pts4.controller;

import com.google.zxing.WriterException;
import nl.pts4.utils.QRCodeGenerator;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class IdentificationController {

    @RequestMapping(value = "id/create", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] createQrCode(HttpServletResponse response) throws WriterException, IOException {
        return QRCodeGenerator.generate("http://lmgtfy.com/?q=oh+shit+waddup", "png");
    }

}
